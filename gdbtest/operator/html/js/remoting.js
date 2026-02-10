var org;
(function (org) {
    var nova;
    (function (nova) {
        var html;
        (function (html) {
            var remoting;
            (function (remoting) {
                var HtmlResult = (function () {
                    function HtmlResult() {
                    }
                    return HtmlResult;
                }());
                var ModalResult = (function () {
                    function ModalResult() {
                    }
                    return ModalResult;
                }());
                var ValResult = (function () {
                    function ValResult() {
                    }
                    return ValResult;
                }());
                var RemoveClassResult = (function () {
                    function RemoveClassResult() {
                    }
                    return RemoveClassResult;
                }());
                var AddClassResult = (function () {
                    function AddClassResult() {
                    }
                    return AddClassResult;
                }());
                var PropResult = (function () {
                    function PropResult() {
                    }
                    return PropResult;
                }());
                var ClearTimerCommand = (function () {
                    function ClearTimerCommand() {
                    }
                    return ClearTimerCommand;
                }());
                var Response = (function () {
                    function Response() {
                    }
                    return Response;
                }());
                function get(pathAndQuery, result) {
                    if (result === void 0) { result = null; }
                    run("GET", pathAndQuery, result);
                }
                remoting.get = get;
                function post(pathAndQuery, result) {
                    if (result === void 0) { result = null; }
                    run("POST", pathAndQuery, null);
                }
                remoting.post = post;
                function scheduleGet(pathAndQuery, timerName, interval) {
                    scheduleRun("GET", pathAndQuery, timerName, interval);
                }
                remoting.scheduleGet = scheduleGet;
                function schedulePost(pathAndQuery, timerName, interval) {
                    scheduleRun("POST", pathAndQuery, timerName, interval);
                }
                remoting.schedulePost = schedulePost;
                var timers = [];
                function scheduleRun(type, pathAndQuery, timerName, interval) {
                    clearTimer[timerName];
                    var timerId = window.setInterval(function () { run(type, pathAndQuery, null); }, interval);
                    timers[timerName] = timerId;
                }
                function clearTimer(timerName) {
                    var timerId = timers[timerName];
                    if (timerId != null) {
                        window.clearInterval(timerId);
                    }
                    timers[timerName] = null;
                }
                function run(type, pathAndQuery, result) {
                    $.ajax({ url: pathAndQuery,
                        type: type,
                        dataType: "json",
                        cache: false,
                        success: function (response) {
                            if (response.clearTimerCommands != null) {
                                response.clearTimerCommands.forEach(function (item) {
                                    clearTimer(item.timerName);
                                });
                            }
                            if (response.htmlResults != null) {
                                response.htmlResults.forEach(function (item) {
                                    $("#" + item.id).html(item.html);
                                });
                            }
                            if (response.appendResults != null) {
                                response.appendResults.forEach(function (item) {
                                    var template = document.createElement("template");
                                    template.innerHTML = item.html;
                                });
                            }
                            if (response.modalResults != null) {
                                response.modalResults.forEach(function (item) {
                                    $("#" + item.id).modal(item.option);
                                });
                            }
                            if (response.valResults != null) {
                                response.valResults.forEach(function (item) {
                                    $("#" + item.id).val(item.val);
                                });
                            }
                            if (response.removeClassResults != null) {
                                response.removeClassResults.forEach(function (item) {
                                    $("#" + item.id).removeClass(item.class_);
                                });
                            }
                            if (response.addClassResults != null) {
                                response.addClassResults.forEach(function (item) {
                                    $("#" + item.id).addClass(item.class_);
                                });
                            }
                            if (response.propResults != null) {
                                response.propResults.forEach(function (item) {
                                    if (item.value != null) {
                                        $("#" + item.id).prop(item.prop, item.value);
                                    }
                                    else {
                                        $("#" + item.id).prop(item.prop);
                                    }
                                });
                            }
                            if (response.script != null) {
                                try {
                                    eval(response.script);
                                }
                                catch (ex) {
                                }
                            }
                            if (response.location != null) {
                                document.location.href = response.location;
                            }
                            if (result != null) {
                                if (response.result != null) {
                                    result(JSON.parse(response.result));
                                }
                                else {
                                    result(null);
                                }
                            }
                        },
                        error: function (xhr) {
                            alert("Error: " + xhr.status + " " + xhr.statusText);
                        }
                    });
                }
            })(remoting = html.remoting || (html.remoting = {}));
        })(html = nova.html || (nova.html = {}));
    })(nova = org.nova || (org.nova = {}));
})(org || (org = {}));
