var scheduleMember;
(function (scheduleMember) {
    var SelectBlock = (function () {
        function SelectBlock(selectedHour, selectedMinute, endHour, endMinute) {
            this.backgroundElement = document.getElementById("modalBackground");
            this.selectedHour = selectedHour;
            this.selectedMinute = selectedMinute;
            this.endHour = endHour;
            this.endMinute = endMinute;
            this.maxDuration = endHour * 60 + endMinute - (selectedHour * 60 + selectedMinute);
            this.maxHours = endHour - selectedHour;
            this.maxMinutes = endMinute - selectedMinute;
            if (this.maxMinutes < 0) {
                this.maxMinutes += 60;
                this.maxHours--;
            }
        }
        SelectBlock.prototype.changeEnd = function () {
            var minutes = this.minuteEnd.selectedIndex * 15;
            var duration = this.hourEnd.selectedIndex * 60 + minutes - this.selectedMinute;
            console.log("maxHours:" + this.maxHours);
            console.log("maxMinutes:" + this.maxMinutes);
            console.log("duration:" + duration);
            console.log("maxDuration:" + this.maxDuration);
            if (duration > this.maxDuration) {
                this.minuteEnd.selectedIndex = this.endMinute / 15;
            }
            minutes -= this.selectedMinute;
            if ((this.hourEnd.selectedIndex == 0) && (minutes < 0)) {
                this.minuteEnd.selectedIndex = this.selectedMinute / 15;
                minutes = 0;
            }
            if (minutes >= 0) {
                this.hour.selectedIndex = this.hourEnd.selectedIndex;
                this.minute.selectedIndex = minutes / 15;
            }
            else {
                this.hour.selectedIndex = this.hourEnd.selectedIndex - 1;
                minutes += 60;
                this.minute.selectedIndex = minutes / 15;
            }
        };
        SelectBlock.prototype.changeDuration = function () {
            if ((this.hour.selectedIndex == 0) && (this.minute.selectedIndex == 0)) {
                this.minute.selectedIndex = 1;
            }
            if (this.hour.selectedIndex > this.maxHours) {
                this.hour.selectedIndex = this.maxHours;
            }
            if (this.hour.selectedIndex == this.maxHours) {
                var minutes = this.minute.selectedIndex * 15;
                if (minutes > this.maxMinutes) {
                    this.minute.selectedIndex = this.maxMinutes / 15;
                }
            }
            this.hourEnd.selectedIndex = this.hour.selectedIndex;
            var minutes = this.selectedMinute + this.minute.selectedIndex * 15;
            if (minutes >= 60) {
                minutes -= 60;
                this.hourEnd.selectedIndex++;
            }
            this.minuteEnd.selectedIndex = minutes / 15;
        };
        SelectBlock.prototype.hidePopup = function () {
            var _a;
            if (this.popover != null) {
                var cancelPopover = document.getElementById("cancelButton");
                if (cancelPopover != null) {
                    $("#cancelButton").popover('hide');
                }
                this.popover.popover('hide');
                this.popover.popover('dispose');
                this.popover = null;
            }
            (_a = this.currentSlotElement) === null || _a === void 0 ? void 0 : _a.classList.remove("search-result-focus");
            console.log(this.backgroundElement);
            this.backgroundElement.style.display = "none";
        };
        SelectBlock.prototype.showPopup = function (popId, content) {
            this.hidePopup();
            this.currentSlotElement = document.getElementById(popId);
            this.popover = $("#" + popId).popover({
                boundary: "window",
                html: true,
                template: '<div class="popover" role="tooltip" style="max-width:100% !important;width:28em;"><div class="arrow"></div><div class="popover-body"></div></div>',
                content: content,
                sanitize: false
            });
            this.popover.popover('show');
            this.backgroundElement.style.display = "block";
            this.hour = document.getElementById("hour");
            this.minute = document.getElementById("minute");
            this.hourEnd = document.getElementById("hourEnd");
            this.minuteEnd = document.getElementById("minuteEnd");
            this.changeEnd();
        };
        return SelectBlock;
    }());
    scheduleMember.SelectBlock = SelectBlock;
    function newInstance(selectedHour, selectedMinute, endHour, endMinute) {
        scheduleMember.instance = new SelectBlock(selectedHour, selectedMinute, endHour, endMinute);
    }
    scheduleMember.newInstance = newInstance;
})(scheduleMember || (scheduleMember = {}));
