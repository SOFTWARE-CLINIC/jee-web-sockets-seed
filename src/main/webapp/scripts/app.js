window.onload = init;
var socket = new WebSocket('ws://localhost:7001/jee-web-sockets-seed/actions');
socket.onmessage = onMessage;

function onMessage(event) {
    var device = JSON.parse(event.data);
    if (device.action === 'add') {
        printDeviceElement(device);
    }
    if (device.action === 'remove') {
        $('#devicesTable').find('tr[id=' + device.id + ']').remove();
    }
    if (device.action === 'toggle') {
        $('#devicesTable').find('tr[id=' + device.id + ']').find('td[property=status]').text(device.status);
        $('#devicesTable').find('tr[id=' + device.id + ']').find('a[action=toggle]').text('Turn ' + (device.status === 'On' ? 'off' : 'on'));
    }
}

function addDevice(name, type, description) {
    var DeviceAction = {
        action: "add",
        name: name,
        type: type,
        description: description
    };
    socket.send(JSON.stringify(DeviceAction));
}

function removeDevice(element) {
    var DeviceAction = {
        action: "remove",
        id: element
    };
    socket.send(JSON.stringify(DeviceAction));
}

function toggleDevice(element) {
    var DeviceAction = {
        action: "toggle",
        id: element
    };
    socket.send(JSON.stringify(DeviceAction));
}

function printDeviceElement(device) {
    $("#devicesTable").find('tbody')
        .append($('<tr>', {id: device.id})
            .append(
                $('<td>', {property: 'id', "class": "text-right"}).text(device.id),
                $('<td>', {property: 'type'}).text(device.type),
                $('<td>', {property: 'name'}).text(device.name),
                $('<td>', {property: 'status'}).text(device.status),
                $('<td>', {property: 'actions'}).append(
                    $('<a>', {
                        action: 'toggle',
                        href: '#',
                        text: 'Turn ' + (device.status === 'On' ? 'off' : 'on'),
                        click: function () {
                            toggleDevice(device.id);
                            return false;
                        }
                    }),
                    ' | ',
                    $('<a>', {
                        action: 'remove',
                        href: '#',
                        text: 'Remove device',
                        click: function () {
                            removeDevice(device.id);
                            return false;
                        }
                    })
                )
            )
        );
}

function showForm() {
    $('#addDeviceButton').hide();
    $('#addDeviceForm').show();
}

function hideForm() {
    $('#addDeviceForm').hide();
    $('#addDeviceButton').show();
}

function formSubmit() {
    var form = document.getElementById("addDeviceForm");
    var name = form.elements["device_name"].value;
    var type = form.elements["device_type"].value;
    var description = form.elements["device_description"].value;
    hideForm();
    document.getElementById("addDeviceForm").reset();
    addDevice(name, type, description);
}

function init() {
    hideForm();
}