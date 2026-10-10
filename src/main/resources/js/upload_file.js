var uploadButton = arguments[0];
var base64 = arguments[1];
var fileName = arguments[2];

var byteChars = atob(base64);
var byteNums = new Array(byteChars.length);
for (var i = 0; i < byteChars.length; i++) {
    byteNums[i] = byteChars.charCodeAt(i);
}
var file = new File([new Uint8Array(byteNums)], fileName, {type: 'image/png'});
var dt = new DataTransfer();
dt.items.add(file);

var originalClick = HTMLInputElement.prototype.click;
var intercepted = false;

HTMLInputElement.prototype.click = function () {
    if (this.type === 'file') {
        intercepted = true;
        this.files = dt.files;
        this.dispatchEvent(new Event('change', {bubbles: true}));
        return;
    }
    return originalClick.apply(this, arguments);
};

try {
    uploadButton.click();
} finally {
    HTMLInputElement.prototype.click = originalClick;
}

if (!intercepted) {
    throw new Error('File input was not intercepted. The page may use showOpenFilePicker or another API.');
}