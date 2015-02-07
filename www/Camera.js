var CameraWebview = {
  showCamera: function (success, failure) {
    cordova.exec(success, failure, "CameraWebview", "show", []);
  },
  hideCamera: function (success, failure) {
    cordova.exec(success, failure, "CameraWebview", "hide", []);
  }
}

module.exports = CameraWebview;