<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>RT Connection Test</title>
    <r:require module="atmosphere-meteor-jquery"/>
    <r:layoutResources/>
%{--
	<asset:javascript src="atmosphere-meteor-jquery.js"/>
--}%
    <style>
    fieldset {
        float: left;
    }

    button {
        float: right;
    }
    </style>
</head>

<body>
    <fieldset >
        <legend>RT Connection</legend>
        <textarea id="console" readonly="readonly" cols="100" rows="50"></textarea><br>
        <button id="connect">Connect</button><br>
        <textarea id="input" cols="100" rows="3"></textarea><br>
        <button id="push">Push</button><br>
    </fieldset>

    <script type="text/javascript">
        var request = {
            contentType: "application/json",
            shared: false,
            transport: 'websocket',
            fallbackTransport: 'long-polling',
            trackMessageLength: true,
            url: '<g:createLink uri="/atmosphere/rtm" />',
            timeout: 3000000000000
        }

        var socket = $.atmosphere;
        var subscription;

        function log(message) {
            $("#console").append(message + "\n");
            $("#console").scrollTop($("#console").height());
        }

        $(document).ready(function () {
            $("#connect").click(function () {
                if (subscription === null || subscription === undefined) {
                    request.onOpen = function (response) {
                        log('Connected to websocket using transport: ' + response.transport);
                        console.log(response);
                    };
                    request.onReconnect = function (request, response) {
                        log('Reconnecting to websocket...');
                        console.log(response);
                    };
                    request.onMessage = function (response) {
                        log(response.responseBody);
                        console.log(response);
                    };
                    request.onError = function (response) {
                        log('Websocket error: ' + response);
                        console.log(response);
                    };
                    request.onTransportFailure = function (errorMsg, request) {
                        log('Websocket transport error: ' + errorMsg);
                        console.log(errorMsg);
                    };
                    request.onClose = function (response) {
                        log('Websocket closed');
                        subscription = null;
                        $(this).html("Connect")
                        console.log(response);
                    };
                    subscription = socket.subscribe(request);
                    $(this).html("Disconnect")
                } else {
                    socket.unsubscribe();
                    subscription = null;
                    $(this).html("Connect")
                }
            });

            $("#push").click(function () {
                if (subscription !== null && subscription !== undefined) {
                    subscription.push($('#input').val());
                    $('#input').val('');
                }
            });
        });
    </script>
</body>
</html>