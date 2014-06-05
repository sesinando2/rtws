function enable(input) {
    $(input).removeAttr("disabled");
}

function disable(input) {
    $(input).attr("disabled", "disabled");
}

function tokenRestrictionTypeChange(form) {
    var submit = $(form).find("input[type=submit]");
    var type = form.type.value;
    toggleAddRestrictionFields(form)
    switch (type) {
        case "UPLOAD":   // Fall through
        case "DOWNLOAD": // Fall through
        case "MESSAGE":  // Fall through
            enable(submit);
            break;
        default:
            disable(submit)
    }
}

function toggleAddRestrictionFields(form) {
    var type = form.type.value;
    if (type == "UPLOAD") {
        enable(form.fileCount);
        $(form).children(".upload_field").show()
    } else {
        $(form).children(".upload_field").hide()
        disable(form.fileCount);
    }

    if (type == "DOWNLOAD") {
        enable(form.file);
        enable(form.downloadCount);
        $(form).children(".download_field").show();
    } else {
        $(form).children(".download_field").hide();
        disable(form.file);
        disable(form.downloadCount);
    }

    if (type == "MESSAGE") {
        enable(form.message);
        enable(form.readCount);
        enable(form.responseCount);
        $(form).children(".message_field").show();
    } else {
        $(form).children(".message_field").hide();
        disable(form.message);
        disable(form.readCount);
        disable(form.responseCount);
    }
}