var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var nova;
(function (nova) {
    var ui;
    (function (ui) {
        var media;
        (function (media) {
            var Camera = (function () {
                function Camera(cameraElementId, canvasElementId) {
                    this.videoElement = document.getElementById(cameraElementId);
                    this.canvasElement = document.getElementById(canvasElementId);
                }
                Camera.prototype.videoOn = function () {
                    return __awaiter(this, void 0, void 0, function () {
                        var _a;
                        var _this = this;
                        return __generator(this, function (_b) {
                            switch (_b.label) {
                                case 0:
                                    _a = this.videoElement;
                                    return [4, navigator.mediaDevices.getUserMedia({ video: true })];
                                case 1:
                                    _a.srcObject = _b.sent();
                                    this.videoElement.onloadedmetadata = function () {
                                        console.log("playing4...");
                                        _this.videoElement.play();
                                    };
                                    return [2];
                            }
                        });
                    });
                };
                Camera.prototype.postSnapshot = function (url) {
                    this.canvasElement.getContext('2d').drawImage(this.videoElement, 0, 0, this.canvasElement.width, this.canvasElement.height);
                    var dataURL = this.canvasElement.toDataURL();
                    nova.remote.postStatic(url, JSON.stringify({ dataURL: dataURL }));
                };
                Camera.prototype.startPostSnapshot = function (url, interval) {
                    var _this = this;
                    this.interval = setInterval(function () {
                        _this.canvasElement.getContext('2d').drawImage(_this.videoElement, 0, 0, _this.canvasElement.width, _this.canvasElement.height);
                        var dataURL = _this.canvasElement.toDataURL();
                        nova.remote.postStatic(url, JSON.stringify({ dataURL: dataURL }));
                    }, interval);
                };
                Camera.prototype.stopPostSnapshot = function () {
                    this.videoElement.pause();
                    clearInterval(this.interval);
                };
                return Camera;
            }());
            media.Camera = Camera;
        })(media = ui.media || (ui.media = {}));
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
(function (nova) {
    var ui;
    (function (ui) {
        var validation;
        (function (validation) {
            function provideFeedback(event) {
                var form = event.target;
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add("was-validated");
            }
            validation.provideFeedback = provideFeedback;
        })(validation = ui.validation || (ui.validation = {}));
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
(function (nova) {
    var ui;
    (function (ui) {
        var modal;
        (function (modal_1) {
            function proceed(modalId, headerId, header, bodyId, body, buttonId, code) {
                var button = document.getElementById(buttonId);
                if (headerId != null) {
                    document.getElementById(headerId).innerHTML = header;
                }
                if (bodyId != null) {
                    document.getElementById(bodyId).innerHTML = body;
                }
                var modal = bootstrap.Modal.getOrCreateInstance(document.getElementById(modalId));
                button.onclick = function (event) {
                    modal.hide();
                    if (code != null) {
                        eval(code);
                    }
                };
                modal.show();
            }
            modal_1.proceed = proceed;
            function show(modalId) {
                var modal = bootstrap.Modal.getOrCreateInstance(document.getElementById(modalId));
                modal.show();
            }
            modal_1.show = show;
        })(modal = ui.modal || (ui.modal = {}));
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
(function (nova) {
    var ui;
    (function (ui) {
        var search;
        (function (search) {
            var Picker = (function () {
                function Picker(itemIdPrefix, focusClass) {
                    this.focusClass = focusClass;
                    this.itemIdPrefix = itemIdPrefix;
                    this.currentId = -1;
                    this.itemCount = 0;
                }
                Picker.prototype.reset = function (itemCount) {
                    this.itemCount = itemCount;
                    this.currentId = -1;
                };
                Picker.prototype.process = function (event) {
                    console.log("keyCode:" + event.keyCode);
                    if (event.keyCode == 40) {
                        var lastId = this.currentId;
                        this.currentId++;
                        if (this.currentId >= this.itemCount) {
                            this.currentId = 0;
                        }
                        var item = document.getElementById(this.itemIdPrefix + this.currentId);
                        item.classList.add(this.focusClass);
                        if (lastId >= 0) {
                            document.getElementById(this.itemIdPrefix + lastId).classList.remove(this.focusClass);
                        }
                    }
                    else if (event.keyCode == 38) {
                        var lastId = this.currentId;
                        this.currentId--;
                        if (this.currentId < 0) {
                            this.currentId = this.itemCount - 1;
                        }
                        var item = document.getElementById(this.itemIdPrefix + this.currentId);
                        item.classList.add(this.focusClass);
                        if (lastId >= 0) {
                            document.getElementById(this.itemIdPrefix + lastId).classList.remove(this.focusClass);
                        }
                    }
                    else if (event.keyCode == 13) {
                        var item = document.getElementById(this.itemIdPrefix + this.currentId);
                        item.click();
                    }
                };
                return Picker;
            }());
            search.Picker = Picker;
        })(search = ui.search || (ui.search = {}));
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
(function (nova) {
    var ui;
    (function (ui) {
        var password;
        (function (password_1) {
            function getPasswordScore(password, minLength) {
                var score = 0;
                if (password.length < minLength) {
                    return 0;
                }
                if (password.length >= minLength) {
                    score = /.*[a-z].*/.test(password) ? ++score : score;
                    score = /.*[A-Z].*/.test(password) ? ++score : score;
                    score = /.*[0-9].*/.test(password) ? ++score : score;
                    score = /[^a-zA-Z0-9]/.test(password) ? ++score : score;
                }
                score = /.*pass.*|.*pass.*|.*123.*|.*098.*|.*qwe.*|.*asd.*|.*zxc.*/.test(password) ? --score : score;
                if (password.length > minLength + 2) {
                    ++score;
                }
                if (password.length >> minLength + 4) {
                    ++score;
                }
                if (password.length > minLength + 6) {
                    ++score;
                }
                return score;
            }
            password_1.getPasswordScore = getPasswordScore;
            function toggleVisibility(id, iconId) {
                var inputElement = document.getElementById(id);
                var iconElement = document.getElementById(iconId);
                if (inputElement.type === "password") {
                    inputElement.type = "text";
                    iconElement.classList.remove("bi-eye-fill");
                    iconElement.classList.add("bi-eye-slash-fill");
                }
                else {
                    inputElement.type = "password";
                    iconElement.classList.add("bi-eye-fill");
                    iconElement.classList.remove("bi-eye-slash-fill");
                }
            }
            password_1.toggleVisibility = toggleVisibility;
            function updatePassword(inputid, buttonid) {
                var inputElement = document.getElementById(inputid);
                var password = inputElement.value;
                if (password.length == 0) {
                    $("#" + buttonid).addClass("disabled");
                    $("#" + buttonid).prop("disabled", true);
                }
                else {
                    $("#" + buttonid).removeClass("disabled");
                    $("#" + buttonid).prop("disabled", false);
                }
            }
            password_1.updatePassword = updatePassword;
            function updateNewPasswordProgress(messages, minLength, inputId1, inputId2, buttonId1, buttonId2, feedbackId, progressId, submitId) {
                var inputElement1 = document.getElementById(inputId1);
                var password1 = inputElement1.value;
                var inputElement2 = document.getElementById(inputId2);
                var password2 = inputElement2.value;
                var button = document.getElementById(submitId);
                var score = getPasswordScore(password1, minLength);
                if (submitId != null) {
                    if ((password1 == password2) && (score >= 3)) {
                        $("#" + submitId).removeClass("disabled");
                        $("#" + submitId).prop("disabled", false);
                    }
                    else {
                        $("#" + submitId).addClass("disabled");
                        $("#" + submitId).prop("disabled", true);
                    }
                }
                if (password2.length == 0) {
                    $("#" + buttonId2).addClass("disabled");
                    $("#" + buttonId2).prop("disabled", true);
                }
                else {
                    $("#" + buttonId2).removeClass("disabled");
                    $("#" + buttonId2).prop("disabled", false);
                }
                if (password1.length == 0) {
                    $("#" + buttonId1).addClass("disabled");
                    $("#" + buttonId1).prop("disabled", true);
                }
                else {
                    $("#" + buttonId1).removeClass("disabled");
                    $("#" + buttonId1).prop("disabled", false);
                }
                if (progressId != null) {
                    var barElement = document.getElementById(progressId);
                    var feedbackElement = document.getElementById(feedbackId);
                    if (password1.length == 0) {
                        $("#" + progressId).attr("style", "width:0%");
                    }
                    else if (score >= 5) {
                        $("#" + progressId).attr("style", "width:100%");
                        $("#" + progressId).addClass("bg-success");
                        $("#" + progressId).removeClass("bg-warning");
                        $("#" + progressId).removeClass("bg-danger");
                        $("#" + progressId).addClass("text-light");
                        $("#" + progressId).removeClass("text-dark");
                        feedbackElement.innerHTML = messages[2];
                    }
                    else if (score >= 3) {
                        $("#" + progressId).attr("style", "width:66%");
                        $("#" + progressId).removeClass("bg-success");
                        $("#" + progressId).addClass("bg-warning");
                        $("#" + progressId).removeClass("bg-danger");
                        $("#" + progressId).removeClass("text-light");
                        $("#" + progressId).addClass("text-dark");
                        feedbackElement.innerHTML = messages[1];
                    }
                    else {
                        $("#" + progressId).attr("style", "width:33%");
                        $("#" + progressId).removeClass("bg-success");
                        $("#" + progressId).removeClass("bg-warning");
                        $("#" + progressId).addClass("bg-danger");
                        $("#" + progressId).addClass("text-light");
                        $("#" + progressId).removeClass("text-dark");
                        feedbackElement.innerHTML = messages[0];
                    }
                }
            }
            password_1.updateNewPasswordProgress = updateNewPasswordProgress;
        })(password = ui.password || (ui.password = {}));
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
(function (nova) {
    var ui;
    (function (ui) {
        var remote;
        (function (remote) {
            var RemoteSearchSelect = (function () {
                function RemoteSearchSelect(remoteStateBinding, action, id) {
                    this.selectedColor = "bg-secondary";
                    this.blur = false;
                    this.timer = null;
                    this.remoteStateBinding = remoteStateBinding;
                    this.action = action;
                    this.id = id;
                    this.inputElement = document.getElementById(id);
                    this.optionIndex = -1;
                }
                RemoteSearchSelect.prototype.processBlur = function (event) {
                    var _this = this;
                    if (this.timer != null) {
                        clearTimeout(this.timer);
                    }
                    setTimeout(function () { _this.clearOptions(); }, 200);
                };
                RemoteSearchSelect.prototype.clearOptions = function () {
                    var element = document.getElementById(this.id + "-options");
                    element.innerHTML = "";
                };
                RemoteSearchSelect.prototype.processKeyup = function (event) {
                    var _this = this;
                    if (this.keydownCaptured == false) {
                        console.log("keyup:" + event.keyCode);
                        if (this.timer != null) {
                            clearTimeout(this.timer);
                        }
                        this.timer = setTimeout(function () { _this.post(); }, 400);
                    }
                    this.keydownCaptured = false;
                };
                RemoteSearchSelect.prototype.post = function () {
                    if ((this.inputElement.value == null) || (this.inputElement.value.length == 0)) {
                        return;
                    }
                    var pathAndQuery = this.action + "/options?" + this.remoteStateBinding + "&search=" + encodeURIComponent(this.inputElement.value);
                    console.log("path:" + pathAndQuery);
                    this.optionIndex = -1;
                    if (this.selectedOptionElement != null) {
                        this.selectedOptionElement.classList.remove(this.selectedColor);
                        this.selectedOptionElement.classList.remove("bg-opacity-25");
                    }
                    nova.remote.postStatic(pathAndQuery);
                };
                RemoteSearchSelect.prototype.processKeydown = function (event) {
                    console.log("keydown:" + event.keyCode);
                    var newOptionIndex = null;
                    if (event.keyCode == 40) {
                        newOptionIndex = this.optionIndex + 1;
                    }
                    else if (event.keyCode == 38) {
                        newOptionIndex = this.optionIndex - 1;
                    }
                    else if (event.keyCode == 13) {
                        if (this.selectedOptionElement != null) {
                            console.log("click:" + newOptionIndex);
                            this.selectedOptionElement.click();
                            return;
                        }
                    }
                    console.log("index:" + newOptionIndex);
                    if (newOptionIndex != null) {
                        this.keydownCaptured = true;
                        var optionElement = document.getElementById(this.id + "-option-" + newOptionIndex);
                        if (optionElement != null) {
                            if (this.selectedOptionElement != null) {
                                this.selectedOptionElement.classList.remove(this.selectedColor);
                                this.selectedOptionElement.classList.remove("bg-opacity-25");
                            }
                            this.optionIndex = newOptionIndex;
                            this.selectedOptionElement = optionElement;
                            this.selectedOptionElement.classList.add(this.selectedColor);
                            this.selectedOptionElement.classList.add("bg-opacity-25");
                        }
                    }
                };
                return RemoteSearchSelect;
            }());
            remote.RemoteSearchSelect = RemoteSearchSelect;
        })(remote = ui.remote || (ui.remote = {}));
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
(function (nova) {
    var ui;
    (function (ui) {
        function toggleOptionalTextInput(checkId, inputId, reversed) {
            var checkElement = document.getElementById(checkId);
            var inputElement = document.getElementById(inputId);
            if (checkElement.checked != reversed) {
                inputElement.setAttribute("disabled", "true");
            }
            else {
                inputElement.removeAttribute("disabled");
            }
        }
        ui.toggleOptionalTextInput = toggleOptionalTextInput;
        function initializeTabs(id) {
            var triggerTabList = [].slice.call(document.querySelectorAll(id));
            triggerTabList.forEach(function (triggerEl) {
                var tabTrigger = new bootstrap.Tab(triggerEl);
                triggerEl.addEventListener('click', function (event) {
                    event.preventDefault();
                    tabTrigger.show();
                });
            });
        }
        ui.initializeTabs = initializeTabs;
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
