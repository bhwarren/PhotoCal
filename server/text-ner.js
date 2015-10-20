var http = require('http');

var data = "'a - . H Interested in  V  , Graduate or Medical  Carolina Neuroscience Club is hosting a panel of graduate students, medical students,  and medical residents!    September 14th Like our Facebook page: Carolina Neuroscience Club  Genome 5100 Join the listserv: carolinaneuroscienceclub@gmail.com  CNC website: http://carolinaneuroscience.web.unc.edu/ ve experience in neuroscience and are  actually making a career out of it, so if you are interested in the brain this   panel is for you!'";
//var data = "'a - . H Interested in  V  , Graduate or Medical  Carolina Neuroscience Club is hosting a panel of graduate students, medical students,  and medical residents!    September 14th Like our Facebook page: Carolina Neuroscience Club  Genome 5100 Join the listserv: carolinaneuroscienceclub@gmail.com  CNC website: http://carolinaneuroscience.web.unc.edu/ ve experience in neuroscience and are  actually making a career out of it, so if you are interested in the brain this   panel is for you!'"; 
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
