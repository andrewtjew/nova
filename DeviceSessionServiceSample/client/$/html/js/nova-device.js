var nova;
(function (nova) {
    var device;
    (function (device) {
        function initialize(path) {
            var timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
            window.location.href = encodeURI(path + "&timeZone=" + timeZone);
        }
        device.initialize = initialize;
    })(device = nova.device || (nova.device = {}));
})(nova || (nova = {}));
