var appointment;
(function (appointment_1) {
    var Appointment = (function () {
        function Appointment() {
            this.testTime = 0;
            this.backgroundElement = document.getElementById("modalBackground");
        }
        Appointment.prototype.setMemberIds = function (memberIds) {
            var _this = this;
            this.memberIds = memberIds;
            this.ladderElements = new Object();
            for (var _i = 0, memberIds_1 = memberIds; _i < memberIds_1.length; _i++) {
                var memberId = memberIds_1[_i];
                this.ladderElements[memberId] = document.getElementById("ladder" + memberId);
            }
            if (this.timer != undefined) {
                clearInterval(this.timer);
            }
            this.timer = setInterval(function () { _this.update(); }, 30000);
        };
        Appointment.prototype.update = function () {
            this.testTime += 0.01;
            if (this.testTime > 50) {
                this.testTime = 0;
            }
            for (var _i = 0, _a = this.memberIds; _i < _a.length; _i++) {
                var memberId = _a[_i];
                var element = this.ladderElements[memberId];
                element.style.height = this.testTime + "em";
            }
        };
        Appointment.prototype.test = function () {
            alert(this.pop);
            this.hidePop();
        };
        Appointment.prototype.hidePop = function () {
            if (this.pop != null) {
                var cancelPopover = document.getElementById("cancelButton");
                if (cancelPopover != null) {
                    $("#cancelButton").popover('hide');
                }
                this.pop.popover('hide');
                this.pop.popover('dispose');
                this.pop = null;
            }
            this.currentSlotElement.classList.remove("search-result-focus");
            console.log(this.backgroundElement);
            this.backgroundElement.style.display = "none";
        };
        Appointment.prototype.processKey = function (event) {
            this.activeForm.process(event);
        };
        Appointment.prototype.showPopup = function (popId, content) {
            this.hidePop();
            this.currentSlotElement = document.getElementById(popId);
            this.currentSlotElement.classList.add("search-result-focus");
            this.pop = $("#" + popId).popover({
                boundary: "viewport",
                html: true,
                template: '<div class="popover" role="tooltip" style="max-width:100% !important;width:48em;"><div class="arrow"></div><div class="popover-body"></div></div>',
                content: content,
                sanitize: false
            });
            this.pop.popover('show');
            this.backgroundElement.style.display = "block";
        };
        Appointment.prototype.showMenu = function (popId, content) {
            this.hidePop();
            this.currentSlotElement = document.getElementById(popId);
            this.currentSlotElement.classList.add("search-result-focus");
            this.pop = $("#" + popId).popover({
                boundary: "viewport",
                html: true,
                content: content,
                sanitize: false
            });
            this.pop.popover('show');
        };
        Appointment.prototype.showNewAppointment = function (popId, content) {
            this.showPopup(popId, content);
            this.activeForm = new ui.AutoCompleteForm("item", "search-result-focus");
            document.getElementById("book").disabled = true;
            var clientNameInput = document.getElementById("clientName");
            var appointment = this;
            clientNameInput.onkeydown = function (event) {
                appointment.activeForm.process(event);
            };
            clientNameInput.oninput = function () {
                var data = new Object();
                data[clientNameInput.name] = clientNameInput.value;
                {
                    var birthdayYearInput = document.getElementById("birthdayYear");
                    data[birthdayYearInput.name] = birthdayYearInput.value;
                }
                {
                    var birthdayMonthInput = document.getElementById("birthdayMonth");
                    data[birthdayMonthInput.name] = birthdayMonthInput.value;
                }
                {
                    var birthdayDayInput = document.getElementById("birthdayDay");
                    data[birthdayDayInput.name] = birthdayDayInput.value;
                }
                nova.remote.call("POST", "/appointment/client/search", data, true);
            };
        };
        Appointment.prototype.cancelAppointment = function (appointmentId) {
            alert(appointmentId);
        };
        Appointment.prototype.selectClient = function (clientId) {
            nova.remote.call("POST", "/appointment/client?clientId=" + clientId, null, true);
            document.getElementById("book").disabled = false;
        };
        Appointment.prototype.resetForm = function (itemCount) {
            this.activeForm.reset(itemCount);
            document.getElementById("book").disabled = true;
        };
        return Appointment;
    }());
    appointment_1.Appointment = Appointment;
    appointment_1.instance = new Appointment();
})(appointment || (appointment = {}));
