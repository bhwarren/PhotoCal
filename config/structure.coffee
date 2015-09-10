# Read more about app structure at http://docs.appgyver.com

module.exports =

  # See styling options for tabs and other native components in app/common/native-styles/ios.css or app/common/native-styles/android.css
  # tabs: [
  #   {
  #     title: "Index"
  #     id: "index"
  #     location: "example#getting-started" # Supersonic module#view type navigation
  #   }
  #   {
  #     title: "Settings"
  #     id: "settings"
  #     location: "example#settings"
  #     #example/views/settings
  #   }
  #   {
  #     title: "Internet"
  #     id: "internet"
  #     location: "http://google.com" # URLs are supported!
  #   }
  #   {
  #     title: "Photo"
  #     id: "photos"
  #     location: "example#photos" # URLs are supported!
  #   }
  # ]


    drawers:
      right:
          id: "rightDrawer"
          location: "example#drawer"
          showOnAppLoad: false
      options:
          animation: "swingingDoor"

    rootView:
        location: "example#photos"

  preloads: [
    {
      id: "learn-more"
      location: "example#learn-more"
    }
    {
      id: "using-the-scanner"
      location: "example#using-the-scanner"
    }
    {
      id: "photos"
      location: "example#photos"
    }
    {
      id: "photos"
      location: "example#photos"
    }

  ]
