var netlibs;
(function (netlibs) {
    var server;
    var LoginResponse = (function () {
        function LoginResponse() {
        }
        return LoginResponse;
    }());
    var RegisterGameTerminalResponse = (function () {
        function RegisterGameTerminalResponse() {
        }
        return RegisterGameTerminalResponse;
    }());
    var OpenPINSessionResponse = (function () {
        function OpenPINSessionResponse() {
        }
        return OpenPINSessionResponse;
    }());
    var token;
    var loginResponse;
    var registerGameTerminalResponse;
    var gameTerminalID;
    var sessionID;
    var PIN;
    var gameID;
    var openPINSessionResponse;
    function Check() {
        $.ajax({
            type: "GET",
            url: server + "/Check",
            contentType: "application/json",
            async: false,
            success: function (result) {
                console.log(result);
            },
            error: function (response) {
                console.log(response);
            },
            crossDomain: true
        });
    }
    netlibs.Check = Check;
    function Login(user, credentials) {
        $.ajax({
            type: "POST",
            url: server + "/Login",
            data: JSON.stringify({
                user: user,
                credentials: credentials
            }),
            dataType: "json",
            contentType: "application/json",
            async: false,
            success: function (response) {
                token = response.token;
                loginResponse = response;
                console.log(token);
            },
            error: function (response) {
                console.log(response);
            },
            crossDomain: true
        });
    }
    netlibs.Login = Login;
    function RegisterSite(siteName) {
        $.ajax({
            type: "POST",
            url: server + "/RegisterSite",
            data: JSON.stringify({
                SiteName: siteName
            }),
            dataType: "json",
            contentType: "application/json",
            async: false,
            headers: { "X-Token": token },
            success: function (result) {
            },
            error: function (response) {
                console.log(response);
            },
            crossDomain: true
        });
    }
    netlibs.RegisterSite = RegisterSite;
    function RegisterGameTerminal(key, siteID) {
        $.ajax({
            type: "POST",
            url: server + "/RegisterGameTerminal",
            data: JSON.stringify({
                ComputerName: key,
                GameTerminalName: key,
                IPAddress: key,
                MACAddress: key,
                SiteID: siteID
            }),
            dataType: "json",
            contentType: "application/json",
            async: false,
            headers: { "X-Token": token },
            success: function (response) {
                registerGameTerminalResponse = response;
                gameTerminalID = response.GameTerminalID;
            },
            error: function (response) {
                console.log(response);
            },
            crossDomain: true
        });
    }
    netlibs.RegisterGameTerminal = RegisterGameTerminal;
    function AssignGame(gameID) {
        var gameIDs = [gameID];
        $.ajax({
            type: "POST",
            url: server + "/AssignGames",
            data: JSON.stringify({
                GameIDs: gameIDs,
                GameTerminalID: gameTerminalID,
            }),
            dataType: "json",
            contentType: "application/json",
            async: false,
            headers: { "X-Token": token },
            success: function (response) {
            },
            error: function (response) {
                console.log(response);
            },
            crossDomain: true
        });
    }
    netlibs.AssignGame = AssignGame;
    function OpenSession() {
        var result;
        $.ajax({
            type: "POST",
            url: server + "/OpenPINSession",
            data: JSON.stringify({
                PIN: PIN,
                GameTerminalID: gameTerminalID
            }),
            dataType: "json",
            contentType: "application/json",
            async: false,
            headers: { "X-Token": token },
            success: function (response) {
                openPINSessionResponse = response;
                sessionID = openPINSessionResponse.SessionID;
                result = JSON.stringify(response);
            },
            error: function (response) {
                console.log(response);
            },
            crossDomain: true
        });
        return result;
    }
    netlibs.OpenSession = OpenSession;
    function Register(_server, key, siteID, _gameID, pin) {
    }
    netlibs.Register = Register;
    function Register2(_server, _gameID, pin) {
        var key = pin;
        server = _server;
        PIN = pin;
        gameID = _gameID;
        Login(key, "none");
        RegisterGameTerminal(key, 1);
        AssignGame(gameID);
    }
    netlibs.Register2 = Register2;
    function TestRegister() {
        Register("http://18.211.84.163:10002/gt", "debug", 1, 19, "555555");
    }
    netlibs.TestRegister = TestRegister;
    var TicketPlayRequest = (function () {
        function TicketPlayRequest() {
        }
        return TicketPlayRequest;
    }());
    var MultiTicketPlayRequest = (function () {
        function MultiTicketPlayRequest() {
        }
        return MultiTicketPlayRequest;
    }());
    function Hello() {
        alert('hello');
    }
    netlibs.Hello = Hello;
    function create_UUID() {
        var dt = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = (dt + Math.random() * 16) % 16 | 0;
            dt = Math.floor(dt / 16);
            return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
        return uuid;
    }
    function GamePlay(uniqueID, sales, denomination, linesPlayed, creditsPerLinesPlayed, freeSpin, superSpin, option, wildChoice) {
        var ticketPlayRequest = new TicketPlayRequest();
        ticketPlayRequest.SessionID = sessionID;
        ticketPlayRequest.GameTerminalID = gameTerminalID;
        ticketPlayRequest.GameID = gameID;
        ticketPlayRequest.PIN = PIN;
        ticketPlayRequest.Denomination = denomination;
        ticketPlayRequest.Sales = sales;
        ticketPlayRequest.LinesPlayed = linesPlayed;
        ticketPlayRequest.CreditsPerLinesPlayed = creditsPerLinesPlayed;
        ticketPlayRequest.UniqueID = uniqueID;
        ticketPlayRequest.FreeSpin = freeSpin;
        ticketPlayRequest.SuperSpin = superSpin;
        ticketPlayRequest.suppressAntee = true;
        ticketPlayRequest.option = option;
        ticketPlayRequest.wildChoice = wildChoice;
        var multiTicketPlayRequest = new MultiTicketPlayRequest();
        multiTicketPlayRequest.TicketPlayRequests = [ticketPlayRequest];
        var request = JSON.stringify(multiTicketPlayRequest);
        console.log("request:" + request);
        var result;
        $.ajax({
            type: "POST",
            url: server + "/MultiTicketPlay",
            data: request,
            dataType: "json",
            contentType: "application/json",
            async: false,
            headers: { "X-Token": token },
            success: function (response) {
                result = JSON.stringify(response);
            },
            error: function (response) {
                console.log(response);
            },
            crossDomain: true
        });
        return result;
    }
    netlibs.GamePlay = GamePlay;
    var gamePlayResult;
    function GamePlayAsync(uniqueID, sales, denomination, linesPlayed, creditsPerLinesPlayed, freeSpin, superSpin, option, wildChoice) {
        var ticketPlayRequest = new TicketPlayRequest();
        ticketPlayRequest.SessionID = sessionID;
        ticketPlayRequest.GameTerminalID = gameTerminalID;
        ticketPlayRequest.GameID = gameID;
        ticketPlayRequest.PIN = PIN;
        ticketPlayRequest.Denomination = denomination;
        ticketPlayRequest.Sales = sales;
        ticketPlayRequest.LinesPlayed = linesPlayed;
        ticketPlayRequest.CreditsPerLinesPlayed = creditsPerLinesPlayed;
        ticketPlayRequest.UniqueID = uniqueID;
        ticketPlayRequest.FreeSpin = freeSpin;
        ticketPlayRequest.SuperSpin = superSpin;
        ticketPlayRequest.suppressAntee = true;
        ticketPlayRequest.option = option;
        ticketPlayRequest.wildChoice = wildChoice;
        var multiTicketPlayRequest = new MultiTicketPlayRequest();
        multiTicketPlayRequest.TicketPlayRequests = [ticketPlayRequest];
        var request = JSON.stringify(multiTicketPlayRequest);
        gamePlayResult = null;
        $.ajax({
            type: "POST",
            url: server + "/MultiTicketPlay",
            data: request,
            dataType: "json",
            contentType: "application/json",
            async: true,
            headers: { "X-Token": token },
            success: function (response) {
                gamePlayResult = JSON.stringify(response);
                console.log(gamePlayResult);
            },
            error: function (response) {
                console.log(response);
                gamePlayResult = null;
            },
            crossDomain: true
        });
    }
    netlibs.GamePlayAsync = GamePlayAsync;
    function GetGamePlayResult() {
        return gamePlayResult;
    }
    netlibs.GetGamePlayResult = GetGamePlayResult;
    function GamePlay_UUID(sales, denomination, linesPlayed, creditsPerLinesPlayed, freeSpin, superSpin, option, wildChoice) {
        var uuid = create_UUID();
        GamePlayAsync(uuid, sales, denomination, linesPlayed, creditsPerLinesPlayed, freeSpin, superSpin, option, wildChoice);
    }
    netlibs.GamePlay_UUID = GamePlay_UUID;
    function testView() {
        var result = GetGamePlayResult();
        document.getElementById("result").innerText = result;
    }
    netlibs.testView = testView;
})(netlibs || (netlibs = {}));
