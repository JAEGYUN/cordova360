var exec = require('cordova/exec');

exports.watchPanorama = function(moviePath, success, error) {
    exec(success, error, "gigaeyes360", "watchPanorama", [moviePath]);
};

exports.watch = function(moviePath, user, password, success, error) {
	exec(success, error, "gigaeyes360", "watch", [moviePath, user, password]);
};