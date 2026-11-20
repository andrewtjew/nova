var nova;
(function (nova) {
    var help;
    (function (help) {
        var Link = (function () {
            function Link() {
            }
            return Link;
        }());
        function isChildOf(child, parent) {
            if (child.parentNode === parent) {
                return true;
            }
            else if (child.parentNode === null) {
                return false;
            }
            else {
                return isChildOf(child.parentNode, parent);
            }
        }
        function show() {
            instance.show(instance.index, false);
        }
        function closeIfTargetIsClicked(ev) {
            if (ev.target == instance.targetElement) {
                instance.close();
            }
        }
        var Document = (function () {
            function Document(targetMargin, zIndex, links) {
                window.addEventListener("resize", show);
                window.addEventListener("scroll", show);
                window.addEventListener("click", closeIfTargetIsClicked);
                this.links = links;
                this.targetMargin = targetMargin;
                this.helperElement = document.getElementById("nova-help-helper");
                this.topicElement = document.getElementById("nova-help-topic");
                this.statusElement = document.getElementById("nova-help-navigation-status");
                this.overlayTopElement = document.getElementById("nova-help-overlay-top");
                this.overlayLeftElement = document.getElementById("nova-help-overlay-left");
                this.overlayRightElement = document.getElementById("nova-help-overlay-right");
                this.overlayBottomElement = document.getElementById("nova-help-overlay-bottom");
                this.closeButton = document.getElementById("nova-help-navigation-close");
                this.nextButton = document.getElementById("nova-help-navigation-next");
                this.backButton = document.getElementById("nova-help-navigation-back");
                var z = zIndex.toString();
                this.overlayTopElement.style.zIndex = z;
                this.overlayLeftElement.style.zIndex = z;
                this.overlayRightElement.style.zIndex = z;
                this.overlayBottomElement.style.zIndex = z;
                this.helperElement.style.zIndex = z;
                this.overlayTopElement.style.display = "block";
                this.overlayLeftElement.style.display = "block";
                this.overlayRightElement.style.display = "block";
                this.overlayBottomElement.style.display = "block";
                this.closeButton.onclick = function () {
                    instance.close();
                };
                this.nextButton.onclick = function () {
                    instance.next();
                };
                this.backButton.onclick = function () {
                    instance.back();
                };
                for (var link in links) {
                    var element = document.getElementById(links[link].topicId);
                    element.style.display = "none";
                    this.topicElement.appendChild(element);
                }
                this.index = 0;
                this.currentTopic = null;
            }
            Document.prototype.getTargetRect = function (targetElement) {
                var unAdjustedTargetRect = targetElement.getBoundingClientRect();
                var targetRect = new DOMRect(unAdjustedTargetRect.x - this.targetMargin, unAdjustedTargetRect.y - this.targetMargin, unAdjustedTargetRect.width + 2 * this.targetMargin, unAdjustedTargetRect.height + 2 * this.targetMargin);
                return targetRect;
            };
            Document.prototype.showOverlayAroundTarget = function (targetRect) {
                var innerWidth = window.innerWidth;
                var innerHeight = window.innerHeight;
                var targetTop = targetRect.top - this.targetMargin;
                var targetLeft = targetRect.left - this.targetMargin;
                var targetBottom = targetRect.bottom + this.targetMargin;
                var targetRight = targetRect.right + this.targetMargin;
                var targetHeight = targetRect.bottom - targetRect.top + 2 * this.targetMargin;
                this.overlayTopElement.style.left = "0px";
                this.overlayTopElement.style.top = "0px";
                this.overlayTopElement.style.width = innerWidth + "px";
                this.overlayTopElement.style.height = targetTop + "px";
                this.overlayLeftElement.style.left = "0px";
                this.overlayLeftElement.style.top = targetTop + "px";
                this.overlayLeftElement.style.width = targetLeft + "px";
                this.overlayLeftElement.style.height = targetHeight + "px";
                this.overlayRightElement.style.left = targetRight + "px";
                this.overlayRightElement.style.top = targetTop + "px";
                this.overlayRightElement.style.width = innerWidth + "px";
                this.overlayRightElement.style.height = targetHeight + "px";
                this.overlayBottomElement.style.left = "0px";
                this.overlayBottomElement.style.top = targetBottom + "px";
                this.overlayBottomElement.style.width = innerWidth + "px";
                this.overlayBottomElement.style.height = (innerHeight - targetBottom) + "px";
            };
            Document.prototype.showOverlay = function () {
                var innerWidth = window.innerWidth;
                var innerHeight = window.innerHeight;
                var targetTop = innerHeight / 2;
                var targetLeft = innerWidth / 2;
                var targetBottom = targetTop;
                var targetRight = targetLeft;
                var targetHeight = 0;
                this.overlayTopElement.style.left = "0px";
                this.overlayTopElement.style.top = "0px";
                this.overlayTopElement.style.width = innerWidth + "px";
                this.overlayTopElement.style.height = targetTop + "px";
                this.overlayLeftElement.style.left = "0px";
                this.overlayLeftElement.style.top = targetTop + "px";
                this.overlayLeftElement.style.width = targetLeft + "px";
                this.overlayLeftElement.style.height = targetHeight + "px";
                this.overlayRightElement.style.left = targetRight + "px";
                this.overlayRightElement.style.top = targetTop + "px";
                this.overlayRightElement.style.width = innerWidth + "px";
                this.overlayRightElement.style.height = targetHeight + "px";
                this.overlayBottomElement.style.left = "0px";
                this.overlayBottomElement.style.top = targetBottom + "px";
                this.overlayBottomElement.style.width = innerWidth + "px";
                this.overlayBottomElement.style.height = (innerHeight - targetBottom) + "px";
            };
            Document.prototype.show = function (index, scroll) {
                if (scroll === void 0) { scroll = true; }
                this.index = index;
                var link = this.links[this.index];
                if (this.targetElement != null) {
                    if (this.targetElement.classList.contains("dropdown-menu")) {
                        this.targetElement.classList.remove("show");
                    }
                }
                if (link.targetId != null) {
                    this.targetElement = document.getElementById(link.targetId);
                    if (this.targetElement.classList.contains("dropdown-menu")) {
                        this.targetElement.classList.add("show");
                    }
                    if (scroll) {
                        this.targetElement.scrollIntoView();
                    }
                    this.targetRect = this.getTargetRect(this.targetElement);
                    this.showOverlayAroundTarget(this.targetRect);
                    this.showHelperToTarget(document.getElementById(link.topicId));
                }
                else {
                    this.showOverlay();
                    this.showHelper(document.getElementById(link.topicId));
                }
                if (this.index == 0) {
                    this.backButton.style.display = "none";
                }
                else {
                    this.backButton.style.display = "block";
                }
                if (this.index >= this.links.length - 1) {
                    this.nextButton.style.display = "none";
                }
                else {
                    this.nextButton.style.display = "block";
                }
                this.statusElement.innerText = (index + 1) + "/" + this.links.length;
            };
            Document.prototype.showHelperToTarget = function (topic) {
                this.helperElement.style.display = "block";
                if (this.currentTopic != null) {
                    this.currentTopic.style.display = "none";
                }
                topic.style.display = "block";
                this.currentTopic = topic;
                var targetCenter = this.targetRect.left + this.targetRect.width / 2;
                var helperX = targetCenter - this.helperElement.offsetWidth / 2;
                var scrollX = window.scrollX;
                var scrollY = window.scrollY;
                if (helperX < scrollX) {
                    helperX = scrollX;
                }
                this.helperElement.style.left = helperX + "px";
                var pointerX = targetCenter - helperX + scrollX;
                this.helperElement.style.setProperty("--nova-help-helper-left", pointerX + "px");
                var topLength = this.targetRect.top - scrollY;
                var bottomLength = scrollY + window.innerHeight - this.targetRect.bottom;
                if (topLength < bottomLength) {
                    this.helperElement.style.setProperty("--nova-help-helper-border-width", "0 0.5em 1em");
                    this.helperElement.style.setProperty("--nova-help-helper-top", "-1em");
                    this.helperElement.style.setProperty("--nova-help-helper-bottom", "100%");
                    this.helperElement.style.top = (scrollY + this.targetRect.bottom) + "px";
                }
                else {
                    this.helperElement.style.setProperty("--nova-help-helper-border-width", "1em 0.5em 0");
                    this.helperElement.style.setProperty("--nova-help-helper-top", "100%");
                    this.helperElement.style.setProperty("--nova-help-helper-bottom", "-1em");
                    console.log("offset=" + this.helperElement.offsetHeight);
                    console.log("client=" + this.helperElement.clientHeight);
                    console.log("bounding=" + this.helperElement.getBoundingClientRect().height);
                    var bottom = window.getComputedStyle(this.helperElement, ':after').getPropertyValue('bottom');
                    var heightAdjust = -2 * parseInt(bottom.substring(0, bottom.indexOf("px")));
                    console.log("h=" + heightAdjust);
                    this.helperElement.style.top = (scrollY + this.targetRect.top - this.helperElement.offsetHeight - this.targetMargin - heightAdjust) + "px";
                }
                this.helperElement.style.marginLeft = "0";
                this.helperElement.style.marginTop = "1em";
            };
            Document.prototype.showHelper = function (topic) {
                this.helperElement.style.display = "block";
                if (this.currentTopic != null) {
                    this.currentTopic.style.display = "none";
                }
                topic.style.display = "block";
                this.currentTopic = topic;
                this.helperElement.style.left = "50%";
                this.helperElement.style.top = "50%";
                var rect = this.helperElement.getBoundingClientRect();
                console.log("rect:width=" + rect.width + ",height=" + rect.height);
                this.helperElement.style.marginLeft = (-rect.width / 2) + "px";
                this.helperElement.style.marginTop = (-rect.height / 2) + "px";
                this.helperElement.style.setProperty("--nova-help-helper-border-width", "0");
            };
            Document.prototype.alertTest = function () {
                alert("hello");
            };
            Document.prototype.next = function () {
                var _this = this;
                if (this.index < this.links.length - 1) {
                    this.show(this.index + 1);
                    this.helperElement.style.display = "none";
                    setTimeout(function () { _this.helperElement.style.display = "block"; }, 200);
                }
            };
            Document.prototype.back = function () {
                var _this = this;
                if (this.index > 0) {
                    this.show(this.index - 1);
                    this.helperElement.style.display = "none";
                    setTimeout(function () { _this.helperElement.style.display = "block"; }, 200);
                }
            };
            Document.prototype.close = function () {
                if (this.targetElement != null) {
                    if (this.targetElement.classList.contains("dropdown-menu")) {
                        this.targetElement.classList.remove("show");
                    }
                }
                window.removeEventListener("resize", show);
                window.removeEventListener("scroll", show);
                window.removeEventListener("click", closeIfTargetIsClicked);
                this.helperElement.style.display = "none";
                this.overlayTopElement.style.display = "none";
                this.overlayLeftElement.style.display = "none";
                this.overlayRightElement.style.display = "none";
                this.overlayBottomElement.style.display = "none";
                instance = null;
            };
            return Document;
        }());
        var instance;
        function activate(startIndex, targetMargin, zIndex, links) {
            instance = new Document(targetMargin, zIndex, links);
            instance.show(startIndex);
        }
        help.activate = activate;
    })(help = nova.help || (nova.help = {}));
})(nova || (nova = {}));
