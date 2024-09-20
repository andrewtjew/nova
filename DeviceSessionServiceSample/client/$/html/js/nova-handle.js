var nova;
(function (nova) {
    var handle;
    (function (handle_1) {
        function onTextAreaChange(handle) {
            var textareaElement = document.getElementById("textarea-" + handle);
            var saveButton = document.getElementById("button-save-" + handle);
            var disabled = textareaElement.value.length == 0;
            saveButton.disabled = disabled;
        }
        handle_1.onTextAreaChange = onTextAreaChange;
        function disableSaveButton(handle, undefined) {
            var textareaElement = document.getElementById("textarea-" + handle);
            if (undefined) {
                textareaElement.value = "";
            }
            var saveButton = document.getElementById("button-save-" + handle);
            var disabled = textareaElement.value.length == 0;
            saveButton.disabled = true;
        }
        handle_1.disableSaveButton = disableSaveButton;
    })(handle = nova.handle || (nova.handle = {}));
})(nova || (nova = {}));
