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
var __values = (this && this.__values) || function(o) {
    var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
    if (m) return m.call(o);
    if (o && typeof o.length === "number") return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
    throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
};
var nova;
(function (nova) {
    var remote;
    (function (remote) {
        var Input = (function () {
            function Input() {
            }
            return Input;
        }());
        var Instruction = (function () {
            function Instruction() {
            }
            return Instruction;
        }());
        function getInputElement(parent, input) {
            var element = parent.querySelector("[name='" + input.name + "']");
            if (element == null) {
                console.log("Element not in scope: name=" + input.name);
            }
            return element;
        }
        function getSelectElement(parent, input) {
            var element = parent.querySelector("[name='" + input.name + "']");
            if (element == null) {
                console.log("Element not in scope: name=" + input.name);
            }
            return element;
        }
        function toData(formID, text, trace) {
            if (text == null) {
                return;
            }
            var parent = formID == null ? document : document.getElementById(formID);
            if (parent == null) {
                throw new Error("No element with id=" + formID);
            }
            var data = new Object();
            var inputs = JSON.parse(text);
            for (var i = 0, len = inputs.length; i < len; i++) {
                var input = inputs[i];
                if (trace == true) {
                    console.log("remote:name=" + input.name + ",type=" + input.inputType);
                }
                switch (input.inputType) {
                    case "value":
                        {
                            var element = getInputElement(parent, input);
                            if (element != null) {
                                data[input.name] = element.value;
                            }
                        }
                        break;
                    case "checked":
                        {
                            var element = getInputElement(parent, input);
                            if (element != null) {
                                if (element.checked) {
                                    data[input.name] = "on";
                                }
                            }
                        }
                        break;
                    case "select":
                        {
                            var select = getSelectElement(parent, input);
                            if (select != null) {
                                data[input.name] = select.options[select.selectedIndex].value;
                            }
                        }
                        break;
                    case "radio":
                        var radio = getInputElement(parent, input);
                        if ((radio != null) && (radio.checked)) {
                            data[input.name] = radio.value;
                        }
                        break;
                }
            }
            return data;
        }
        function post(formID, action, text, trace) {
            var data = toData(formID, text, trace);
            call("POST", action, data);
        }
        remote.post = post;
        function get(formID, action, text, trace) {
            var parent = formID == null ? document : document.getElementById(formID);
            var inputs = JSON.parse(text);
            var seperator = "?";
            for (var i = 0, len = inputs.length; i < len; i++) {
                var input = inputs[i];
                if (trace == true) {
                    console.log("input:name=" + input.name + ",type=" + input.inputType);
                }
                switch (input.inputType) {
                    case "value":
                        {
                            var element = getInputElement(parent, input);
                            if (element != null) {
                                action += seperator + input.name + "=" + encodeURIComponent(element.value);
                            }
                        }
                        break;
                    case "checked":
                        {
                            var element = getInputElement(parent, input);
                            if (element != null) {
                                if (element.checked) {
                                    action += seperator + input.name + "=on";
                                }
                            }
                        }
                        break;
                    case "select":
                        {
                            var select = getSelectElement(parent, input);
                            if (select != null) {
                                action += seperator + input.name + "=" + encodeURIComponent(select.options[select.selectedIndex].value);
                            }
                        }
                        break;
                    case "radio":
                        {
                            var element = getInputElement(parent, input);
                            if (element != null) {
                                action += seperator + input.name + "=" + encodeURIComponent(element.value);
                            }
                        }
                        break;
                }
                seperator = "&";
            }
            call("GET", action, null);
        }
        remote.get = get;
        function call(type, pathAndQuery, data) {
            $.ajax({ url: pathAndQuery,
                type: type,
                async: true,
                dataType: "json",
                cache: false,
                data: data,
                success: function (instructions, status, xhr) {
                    run(instructions);
                },
                error: function (xhr) {
                    alert("Error: " + xhr.status + " " + xhr.statusText);
                } });
        }
        remote.call = call;
        function postCheckboxChange(evt, pathAndQuery) {
            return __awaiter(this, void 0, void 0, function () {
                var checkbox;
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0:
                            checkbox = evt.target;
                            return [4, nova.remote.postStatic(pathAndQuery + "&checked=" + checkbox.checked)];
                        case 1: return [2, _a.sent()];
                    }
                });
            });
        }
        remote.postCheckboxChange = postCheckboxChange;
        function getRemote(id, pathAndQuery) {
            return __awaiter(this, void 0, void 0, function () {
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0: return [4, fetch(pathAndQuery, {
                                method: "GET",
                                headers: { 'Content-Type': 'text/html' },
                            }).then(function (response) {
                                if (response.ok) {
                                    return response.text();
                                }
                            })
                                .then(function (text) {
                                document.getElementById(id).innerHTML = text;
                            })];
                        case 1: return [2, _a.sent()];
                    }
                });
            });
        }
        remote.getRemote = getRemote;
        function getStatic(pathAndQuery_1) {
            return __awaiter(this, arguments, void 0, function (pathAndQuery, error) {
                if (error === void 0) { error = null; }
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0: return [4, fetch(pathAndQuery, {
                                method: "GET",
                                headers: { 'Content-Type': 'application/json' },
                            }).then(function (response) {
                                if (response.ok) {
                                    return response.json();
                                }
                                else if (error != null) {
                                    error();
                                }
                            })
                                .then(function (instructions) {
                                run(instructions);
                            })];
                        case 1: return [2, _a.sent()];
                    }
                });
            });
        }
        remote.getStatic = getStatic;
        function postStatic(pathAndQuery_1) {
            return __awaiter(this, arguments, void 0, function (pathAndQuery, data, error) {
                if (data === void 0) { data = null; }
                if (error === void 0) { error = null; }
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0: return [4, fetch(pathAndQuery, {
                                method: "POST",
                                headers: { 'Content-Type': 'application/json' },
                                body: data
                            }).then(function (response) {
                                if (response.ok) {
                                    return response.json();
                                }
                                else if (error != null) {
                                    if (error != null) {
                                        error(error);
                                    }
                                }
                            })
                                .then(function (instructions) {
                                run(instructions);
                            })];
                        case 1: return [2, _a.sent()];
                    }
                });
            });
        }
        remote.postStatic = postStatic;
        function postForNative(pathAndQuery, data) {
            var returnResult;
            var returnSuccess = false;
            $.ajax({ url: pathAndQuery,
                type: "POST",
                contentType: "application/json",
                async: false,
                dataType: "json",
                cache: false,
                data: data,
                success: function (result, status, xhr) {
                    returnResult = result;
                    returnSuccess = true;
                },
                error: function (xhr) {
                } });
            return JSON.stringify({ success: returnSuccess, result: returnResult });
        }
        remote.postForNative = postForNative;
        function submit(event_1) {
            return __awaiter(this, arguments, void 0, function (event, formId) {
                var form, data, params, pair;
                if (formId === void 0) { formId = null; }
                return __generator(this, function (_a) {
                    event.preventDefault();
                    form = (formId == null ? event.currentTarget : document.getElementById(formId));
                    data = new FormData(form);
                    params = new URLSearchParams();
                    for (pair in data) {
                        params.append(pair[0], pair[1]);
                    }
                    console.log("Hello");
                    return [2];
                });
            });
        }
        remote.submit = submit;
        function postUrlEncoded(action, obj) {
            return __awaiter(this, void 0, void 0, function () {
                var params, key;
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0:
                            params = new URLSearchParams();
                            for (key in obj) {
                                params.append(key, obj[key]);
                            }
                            return [4, fetch(action, {
                                    method: "POST",
                                    body: params
                                }).then(function (response) {
                                    if (response.ok) {
                                        return response.json();
                                    }
                                })
                                    .then(function (instructions) {
                                    run(instructions);
                                })];
                        case 1: return [2, _a.sent()];
                    }
                });
            });
        }
        remote.postUrlEncoded = postUrlEncoded;
        function postFormUrlEncoded(id_1) {
            return __awaiter(this, arguments, void 0, function (id, action) {
                var form, data, params, data_1, data_1_1, pair;
                var e_1, _a;
                if (action === void 0) { action = null; }
                return __generator(this, function (_b) {
                    switch (_b.label) {
                        case 0:
                            form = document.getElementById(id);
                            data = new FormData(form);
                            params = new URLSearchParams();
                            try {
                                for (data_1 = __values(data), data_1_1 = data_1.next(); !data_1_1.done; data_1_1 = data_1.next()) {
                                    pair = data_1_1.value;
                                    params.append(pair[0], pair[1].toString());
                                }
                            }
                            catch (e_1_1) { e_1 = { error: e_1_1 }; }
                            finally {
                                try {
                                    if (data_1_1 && !data_1_1.done && (_a = data_1.return)) _a.call(data_1);
                                }
                                finally { if (e_1) throw e_1.error; }
                            }
                            return [4, fetch(action != null ? action : form.action, {
                                    method: "POST",
                                    body: params
                                }).then(function (response) {
                                    if (response.ok) {
                                        return response.json();
                                    }
                                })
                                    .then(function (instructions) {
                                    run(instructions);
                                })];
                        case 1: return [2, _b.sent()];
                    }
                });
            });
        }
        remote.postFormUrlEncoded = postFormUrlEncoded;
        function postForm(id) {
            return __awaiter(this, void 0, void 0, function () {
                var form, data, data, params, data_2, data_2_1, pair;
                var e_2, _a;
                return __generator(this, function (_b) {
                    switch (_b.label) {
                        case 0:
                            form = document.getElementById(id);
                            if (!(form.enctype == "data")) return [3, 2];
                            data = new FormData(form);
                            return [4, fetch(form.action, {
                                    method: "POST",
                                    body: new FormData(form),
                                }).then(function (response) {
                                    if (response.ok) {
                                        return response.json();
                                    }
                                })
                                    .then(function (instructions) {
                                    run(instructions);
                                })];
                        case 1: return [2, _b.sent()];
                        case 2:
                            data = new FormData(form);
                            params = new URLSearchParams();
                            try {
                                for (data_2 = __values(data), data_2_1 = data_2.next(); !data_2_1.done; data_2_1 = data_2.next()) {
                                    pair = data_2_1.value;
                                    params.append(pair[0], pair[1].toString());
                                }
                            }
                            catch (e_2_1) { e_2 = { error: e_2_1 }; }
                            finally {
                                try {
                                    if (data_2_1 && !data_2_1.done && (_a = data_2.return)) _a.call(data_2);
                                }
                                finally { if (e_2) throw e_2.error; }
                            }
                            return [4, fetch(form.action, {
                                    method: "POST",
                                    body: params
                                }).then(function (response) {
                                    if (response.ok) {
                                        return response.json();
                                    }
                                })
                                    .then(function (instructions) {
                                    run(instructions);
                                })];
                        case 3: return [2, _b.sent()];
                    }
                });
            });
        }
        remote.postForm = postForm;
        function postFormData(id_1) {
            return __awaiter(this, arguments, void 0, function (id, action) {
                var form, data;
                if (action === void 0) { action = null; }
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0:
                            form = document.getElementById(id);
                            data = new FormData(form);
                            return [4, fetch(action != null ? action : form.action, {
                                    method: "POST",
                                    body: new FormData(form),
                                }).then(function (response) {
                                    if (response.ok) {
                                        return response.json();
                                    }
                                })
                                    .then(function (instructions) {
                                    run(instructions);
                                })];
                        case 1: return [2, _a.sent()];
                    }
                });
            });
        }
        remote.postFormData = postFormData;
        function run(instructions) {
            if (instructions != null) {
                for (var i = 0, len = instructions.length; i < len; i++) {
                    var instruction = instructions[i];
                    var parameters = instruction.parameters;
                    if (instruction.trace) {
                        console.log("command:" + instruction.command);
                        console.log("parameters:" + parameters);
                    }
                    try {
                        switch (instruction.command) {
                            case "innerHTML":
                                document.getElementById(parameters[0]).innerHTML = parameters[1];
                                break;
                            case "outerHTML":
                                document.getElementById(parameters[0]).outerHTML = parameters[1];
                                break;
                            case "innerText":
                                document.getElementById(parameters[0]).innerText = parameters[1];
                                break;
                            case "value":
                                document.getElementById(parameters[0]).value = parameters[1];
                                break;
                            case "checked":
                                document.getElementById(parameters[0]).checked = parameters[1].toLowerCase() == "true";
                                break;
                            case "alert":
                                alert(parameters[0]);
                                break;
                            case "log":
                                console.log(parameters[0]);
                                break;
                            case "documentObject":
                                document[parameters[0]] = parameters[1];
                                break;
                            case "script":
                                eval(parameters[0]);
                                break;
                            default:
                                if (instruction.trace) {
                                    console.log("nova-remote:invalid command=" + instruction.command);
                                }
                        }
                    }
                    catch (ex) {
                        if (instruction.trace) {
                            console.log("nova-remote:exception=" + ex);
                        }
                    }
                }
            }
        }
    })(remote = nova.remote || (nova.remote = {}));
})(nova || (nova = {}));
