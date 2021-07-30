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
        function toData(text) {
            if (text == null) {
                return;
            }
            var data = new Object();
            var inputs = JSON.parse(text);
            inputs.forEach(function (input) {
                switch (input.inputType) {
                    case "value":
                        data[input.name] = document.getElementById(input.id).value;
                        break;
                    case "checked":
                        data[input.name] = document.getElementById(input.id).checked;
                        break;
                    case "select":
                        {
                            var select = document.getElementById(input.id);
                            data[input.name] = select.options[select.selectedIndex].value;
                        }
                        break;
                    case "radio":
                        var radio = document.getElementById(input.id);
                        if (radio.checked) {
                            data[input.name] = document.getElementById(input.id).value;
                        }
                        break;
                    case "constant":
                        data[input.name] = input.id;
                        break;
                }
            });
            return data;
        }
        function post(action, text, async) {
            var data = toData(text);
            call("POST", action, data, async);
        }
        remote.post = post;
        function get(action, text, async) {
            var inputs = JSON.parse(text);
            var seperator = "?";
            inputs.forEach(function (input) {
                switch (input.inputType) {
                    case "value":
                        action += seperator + input.name + "=" + encodeURIComponent(document.getElementById(input.id).value);
                        break;
                    case "checked":
                        action += seperator + input.name + "=" + encodeURIComponent(document.getElementById(input.id).checked);
                        break;
                    case "select":
                        var select = document.getElementById(input.id);
                        action += seperator + input.name + "=" + encodeURIComponent(select.options[select.selectedIndex].value);
                        break;
                    case "radio":
                        var radio = document.getElementById(input.id);
                        if (radio.checked) {
                            action += seperator + input.name + "=" + encodeURIComponent(radio.value);
                        }
                        break;
                    case "constant":
                        action += seperator + input.name + "=" + encodeURIComponent(input.id);
                        break;
                }
                seperator = "&";
            });
            call("GET", action, null, async);
        }
        remote.get = get;
        function call(type, pathAndQuery, data, async) {
            $.ajax({ url: pathAndQuery,
                type: type,
                async: async,
                dataType: "json",
                cache: false,
                data: data, success: function (instructions) {
                    run(instructions);
                }, error: function (xhr) {
                    alert("Error: " + xhr.status + " " + xhr.statusText);
                } });
        }
        remote.call = call;
        function run(instructions) {
            if (instructions != null) {
                instructions.forEach(function (instruction) {
                    var parameters = instruction.parameters;
                    if (instruction.trace) {
                        console.log("ActionResponse:" + instruction.command);
                        console.log("parameters:" + parameters);
                    }
                    try {
                        switch (instruction.command) {
                            case "value":
                                document.getElementById(parameters[0]).value = parameters[1];
                                break;
                            case "innerHTML":
                                document.getElementById(parameters[0]).innerHTML = parameters[1];
                                break;
                            case "outerHTML":
                                document.getElementById(parameters[0]).outerHTML = parameters[1];
                                break;
                            case "innerText":
                                document.getElementById(parameters[0]).innerText = parameters[1];
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
                        }
                    }
                    catch (ex) {
                        alert("remote exception:" + ex);
                    }
                });
            }
        }
    })(remote = nova.remote || (nova.remote = {}));
})(nova || (nova = {}));
