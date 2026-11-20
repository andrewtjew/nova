var ui;
(function (ui) {
    var AutoCompleteForm = (function () {
        function AutoCompleteForm(itemIdPrefix, focusClass) {
            this.focusClass = focusClass;
            this.itemIdPrefix = itemIdPrefix;
            this.currentId = -1;
            this.itemCount = 0;
        }
        AutoCompleteForm.prototype.reset = function (itemCount) {
            this.itemCount = itemCount;
            this.currentId = -1;
        };
        AutoCompleteForm.prototype.process = function (event) {
            console.log("process" + event.keyCode);
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
        return AutoCompleteForm;
    }());
    ui.AutoCompleteForm = AutoCompleteForm;
    function toggleInput(checkboxId, inputId) {
        var checkbox = document.getElementById(checkboxId);
        var input = document.getElementById(inputId);
        input.disabled = !checkbox.checked;
    }
    ui.toggleInput = toggleInput;
    function showModalMessage(root, header, body) {
        document.getElementById(root + ".header").innerHTML = header;
        document.getElementById(root + ".body").innerHTML = body;
        $('#' + root).modal("show");
    }
    ui.showModalMessage = showModalMessage;
    function validate() {
        var forms = document.getElementsByClassName('needs-validation');
        var validation = Array.prototype.filter.call(forms, function (form) {
            form.addEventListener('submit', function (event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }
    ui.validate = validate;
    function validateOnLoad() {
        window.addEventListener('load', function () {
            var forms = document.getElementsByClassName('needs-validation');
            var validation = Array.prototype.filter.call(forms, function (form) {
                form.addEventListener('submit', function (event) {
                    if (form.checkValidity() === false) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        }, false);
    }
    ui.validateOnLoad = validateOnLoad;
})(ui || (ui = {}));
