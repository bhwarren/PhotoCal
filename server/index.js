var express = require('express');
var bodyParser = require('body-parser');
var spawn = require('child_process').spawn;
var _ = require('underscore');
var formidable = require('formidable');
var util = require('util');
var http = require('http');

var app = express();
app.use(bodyParser.json({limit: '50mb'}));

var port = process.argv[2] || 8088;

var server = app.listen(port, function () {
	var host = server.address().address;
	var port = server.address().port;
	console.log('Processor listening at http://%s:%s', host, port);
});

app.get('/', function (req, res){
  res.writeHead(200, {'Content-Type': 'text/html' });
  var form = '<form action="/upload" enctype="multipart/form-data" method="post">Add a title: <input name="title" type="text" /><br><br><input multiple="multiple" name="upload" type="file" /><br><br><input type="submit" value="Upload" /></form>';
  res.end(form); 
}); 

//when a picture is posted, get the calendar content
app.post('/upload', function(req, res) {
	var form = new formidable.IncomingForm();

	form.parse(req, function(err, fields, files) {
	    	res.writeHead(200, {'content-type': 'text/plain'});
	});
	form.on('file', function(name, file) {
		var picture_path = this.openedFiles[0].path; 
	    	var filename = this.openedFiles[0].name;

		//move the picture to a more appropriate place
		var fs = require('fs');
	        var target_path = './uploads/' + filename;
	        fs.rename(picture_path, target_path, function(err) {
	        	if (err) throw err;
	         	console.log("Upload completed, now doing OCR");
	        });
	
		//#do the OCR
		var text_path = "./uploads/"+filename;
		var ocr_proc = spawn('tesseract', [target_path, text_path, "-psm", "1"]);
	        ocr_proc.on('close', function (code) {
	                console.log('tesseract exited with code ' + code);
	
			//remove the picture file to keep space free
			/*fs.unlink(target_path, function(err){
				if(err) throw err;
	         		console.log("successfully removed picture");
			});
			*/
	
			if(code != 0){
				console.log("tesseract exited oddly, cancelling");
				return;
			}	
	
			//if we get here, then we have text data to send to the entity manager 
			text_path = text_path + ".txt";
			fs.readFile(text_path, 'utf8', function (err,data) {
				if(err) throw err;
			  	console.log("sending this data to be analyzed: "+data);
	
				//send the data to the entity manager
				//var output = JSON.stringify(tag_entities(data));

			        var json_data = JSON.stringify({
			                "file": data,
			                "port": "9191"
			        });
			
			
			        // An object of options to indicate where to post to
			        var post_options = {
			                host: 'localhost',
			                port: '8008',
			                path: '/ner',
			                method: 'POST',
			                headers: {
			                        'Content-Type': 'application/json',
			                }
			        };
			
			        // Set up the request
			        var post_req = http.request(post_options, function(post_res) {
			                post_res.setEncoding('utf8');
			                post_res.on('data', function (response) {
			                        console.log('Response: ' + response);
			                        //res.write(response);
						res.end(response);
			                });
			        });
			
			        // post the data
			        post_req.write(json_data);
			        post_req.end();

	
				//remove the generated ocr file
				/*fs.unlink(text_path, function(err){
	                        	if(err) throw err;
	                        	console.log("successfully removed text file");
	                	});
				*/
			});
							
		});
	
		
		//#str="Friday, March 14, 2008 MIT’s Stata Center 32 Vassar St, Cambridge, MA 32-124 10:30am-12pm"
		
		//curl -H "Content-Type: application/json" -X POST -d "{\"file\":\"$str\",\"port\":\"9191\"}" http://localhost:8008/ner
		//curl -H "Content-Type: application/json" -X POST -d {"file":"$str","port":"9191"} http://localhost:8008/ner
	
	
		/*process.stdout.on('data', function (data) { });
		process.stdin.on('endData',function (data){ })
		process.stderr.on('data', function (data) { });
		process.on('close', function (code) { });
		*/
	});
});

function tag_entities(raw_text) {

	var json_data = JSON.stringify({
		"file": raw_text,
		"port": "9191"
	});


	// An object of options to indicate where to post to
	var post_options = {
		host: 'localhost',
		port: '8008',
		path: '/ner',
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		}
	};

	// Set up the request
	var post_req = http.request(post_options, function(post_res) {
		post_res.setEncoding('utf8');
		post_res.on('data', function (response) {
			console.log('Response: ' + response);
			res.write(response);
		});
	});

	// post the data
	post_req.write(json_data);
	post_req.end();
}
