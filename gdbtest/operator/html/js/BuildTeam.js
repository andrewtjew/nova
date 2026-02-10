var BuildTeam = (function () {
    function BuildTeam() {
        alert('hello');
    }
    BuildTeam.prototype.addPlayer = function (id) {
        var element = document.getElementById(id);
        element.style.display = "none";
    };
    return BuildTeam;
}());
