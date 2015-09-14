# Read more about app structure at http://docs.appgyver.com

module.exports =

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
      id: "photos"
      location: "example#photos"
    }
    {
      id: "events"
      location: "example#events"
    }
    {
      id: "confirm_modal"
      location: "example#confirm_modal"
    }
    {
      id: "about"
      location: "example#about"
    }


  ]
