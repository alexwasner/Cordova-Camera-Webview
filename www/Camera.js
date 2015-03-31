var CameraWebview = {
  start : function(filename, camera, successFunction, errorFunction) {
    camera = camera || 'back';
      cordova.exec(successFunction, errorFunction, "CameraWebview","start", [filename, camera]);
  },
  stop : function(successFunction, errorFunction) {
      cordova.exec(successFunction, errorFunction, "CameraWebview","stop", []);
  },
  status : function(successFunction, errorFunction) {
      cordova.exec(successFunction, errorFunction, "CameraWebview","status", []);
  }
}

module.exports = CameraWebview;