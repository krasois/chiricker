function getUserCard() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/user/card",
        success: function (data) {
            $("#userCardPicture").attr("src", data.profilePicUrl);
            $("#userCardName").text(data.name);
            $("#userCardHandle").text("@" + data.handle);
            $("#userCardBio").text(data.biography);
            let website = $("#userCardWebsite");
            website.text(data.websiteUrl);
            website.attr("href", data.websiteUrl);
        }
    });
}

function getUserProfileInfo() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/user/navbar",
        success: function (data) {
            $("#profileImg").attr("src", data.profilePicUrl);
            $("#profileName").text(data.name);
            $("#profileHandle").text("@" + data.handle);
            $("#profileLink").attr("href", "/@" + data.handle);
        }
    });
}

function setChirickToggling() {
    $("#chirickBtn").mousedown(function () {
        sendChirick();
    });

    let charCounter = $("#charCounter");
    let chirickField = $("#chirickField");
    chirickField.bind('input propertychange', () => {
        charCounter.text(chirickField.val().length)
    });

    $("#chirickForm").focusin(function () {
        $("#chirickControls").show();
        chirickField.attr("rows", 3);
    }).focusout(function () {
        $("#chirickControls").hide();
        chirickField.attr("rows", 1);
    });
}

function sendChirick() {
    let token = $("meta[name='_csrf']").attr("content");
    let headerName = $("meta[name='_csrf_headerName']").attr("content");
    let chirick = $("#chirickField").val();

    let headers = {};
    headers[headerName] = token;

    let data = {};
    data['chirick'] = chirick;
    data['_csrf'] = token;

    $.ajax({
        type: 'POST',
        url: '/chirick/add',
        data: data,
        headers: headers,
        success: function (message) {
            $("#content").prepend(createAlert("alert-success", message));
        },
        error: function (data) {
            $("#content").prepend(createAlert("alert-danger", data.responseText));
        }
    });
}

function createAlert(alertColorClass, message) {
    return $("<div class=\"alert alert-dismissible fade show\" role=\"alert\">").addClass(alertColorClass)
        .append(message)
        .append("</div>")
        .append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>");
}

function getActivityForUser(activityType) {
    let handle = $("#userCardHandle").text();

    let contentHolder = $("#content");

    let loader = $("#loader");
    if (!loader.length) loader =
        $('<div class="d-flex justify-content-center" id="loader">')
            .append($('<div class="loader">'));

    contentHolder.append(loader);

    $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: '/chirick/' + handle + '/' + activityType + '?page=' + page().getNext(),
        error: () => {
            loader.remove();
            contentHolder
                .prepend(createAlert("alert-danger", "Chiricks could not be loaded, refresh the page and try again."))
        },
        success: (data) => {
            loader.remove();

            if (data.length < page().getMaxPerPage()) {
                page().hasNext(false);
            }

            let chiricksCount = $(".modal").length;
            for (let i = 0; i < data.length; i++) {
                let chirick = data[i];
                createChirickDiv(chirick, chiricksCount + i, contentHolder);
            }
        }
    });
}

function getChiricksForUser() {
    getActivityForUser("chiricks");
}

function getCommentsForUser() {
    getActivityForUser("comments");
}

function getRechiricksForUser() {
    getActivityForUser("rechiricks");
}

function getLikesForUser() {
    getActivityForUser("likes");
}

function getCommentsForPost() {
    let postId = $("#postId").text();

    let contentHolder = $("#content");

    let loader = $("#loader");
    if (!loader.length) loader =
        $('<div class="d-flex justify-content-center" id="loader">')
            .append($('<div class="loader">'));

    contentHolder.append(loader);

    $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: '/chirick/comments/' + postId + '/all?page=' + page().getNext(),
        success: (data) => {
            loader.remove();

            if (data.length < page().getMaxPerPage()) {
                page().hasNext(false);
            }

            let chiricksCount = $(".modal").length;
            for (let i = 0; i < data.length; i++) {
                let chirick = data[i];
                createChirickDiv(chirick, chiricksCount + i, contentHolder);
            }
        },
        error: () => {
            loader.remove();
            contentHolder
                .prepend(createAlert("alert-danger", "Comments could not be loaded, refresh the page and try again."))
        }
    });
}

function rechirick(target, idSelector) {
    let element = $(target);

    let chirickId;
    if (idSelector === undefined) {
        chirickId = element.parent().parent().parent().parent().parent() // main div row
            .find('p#id').text();
    } else {
        chirickId = $(idSelector).text();
    }

    let token = $('meta[name="_csrf"]').attr("content");
    let headerName = $('meta[name="_csrf_headerName"]').attr("content");

    let data = {};
    data['id'] = chirickId;
    data['_csrf'] = token;

    let headers = {};
    headers["_csrf"] = token;
    headers["_csrf_headerName"] = headerName;

    $.ajax({
        type: 'POST',
        url: '/chirick/rechirick',
        data: data,
        headers: headers,
        success: (result) => {
            element.text('\u21C6 ' + (result.rechiricksSize > 0 ? result.rechiricksSize : ""));
            if (result.rechiricked) {
                element.addClass("link-color-green-active");
            } else {
                element.removeClass("link-color-green-active");
            }
        },
        error: (error) => {
            $("#content").prepend(createAlert("alert-danger", error.message));
        }
    });
}

function like(target, idSelector) {
    let element = $(target);

    let chirickId;
    if (idSelector === undefined) {
        chirickId = element.parent().parent().parent().parent().parent() // main div row
            .find('p#id').text();
    } else {
        chirickId = $(idSelector).text();
    }

    let token = $('meta[name="_csrf"]').attr("content");
    let headerName = $('meta[name="_csrf_headerName"]').attr("content");

    let data = {};
    data['id'] = chirickId;
    data['_csrf'] = token;

    let headers = {};
    headers["_csrf"] = token;
    headers["_csrf_headerName"] = headerName;

    $.ajax({
        type: 'POST',
        url: '/chirick/like',
        data: data,
        headers: headers,
        success: (result) => {
            element.text('\u2764 ' + (result.likesSize > 0 ? result.likesSize : ""));
            if (result.liked) {
                element.addClass("link-color-red-active");
            } else {
                element.removeClass("link-color-red-active");
            }
        },
        error: (error) => {
            $("#content").prepend(
                createAlert("alert-danger", error.responseText));
        }
    });
}

function comment(target) {
    let form = $("div.row form").has($(target));
    form = $(form[form.length - 1]);

    let commentContent = form.find('textarea').val();
    let chirickId = $(target).attr("data-id");

    let token = $('meta[name="_csrf"]').attr("content");
    let headerName = $('meta[name="_csrf_headerName"]').attr("content");

    let data = {};
    data['id'] = chirickId;
    data['comment'] = commentContent;
    data['_csrf'] = token;

    let headers = {};
    headers["_csrf"] = token;
    headers["_csrf_headerName"] = headerName;

    let contentHolder = $("#content");

    $.ajax({
        type: 'POST',
        url: '/chirick/comment',
        data: data,
        headers: headers,
        success: (result) => {
            let mainDiv = $("div.row").has(form);
            let commentIcon = $(mainDiv[mainDiv.length - 1])
                .find("span.link-color-blue");
            commentIcon
                .addClass("link-color-blue-active")
                .text("\uD83D\uDCAC " + (result.commentsSize > 0 ? result.commentsSize : ""));

            contentHolder.prepend(createAlert("alert-success", "Successfully commented!"))
        },
        error: (error) => {
            let errorMessage = error.responseText;
            contentHolder.prepend(createAlert("alert-danger", errorMessage));
        }
    });
}

function executeWhenInView(element, windowEl, action) {
    let top_of_element = element.offset().top;
    let bottom_of_element = element.offset().top + element.outerHeight();
    let bottom_of_screen = windowEl.scrollTop() + window.innerHeight;
    let top_of_screen = windowEl.scrollTop();

    if ((bottom_of_screen > top_of_element) && (top_of_screen < bottom_of_element)) {
        action();
    }
}

function createChirickDiv(chirick, index, content) {
    let mainRow = $('<div class=\"row\" id="mainRow">');

    let imageCol = $('<div class=\"col\">')
        .append('<img class="img-fluid rounded-circle" alt="Profile image" src="' + chirick.userProfilePicUrl + '"/>');

    let mainCol = $('<div class="col col-lg-10">');

    let userRow = $('<div class="row">');
    if (chirick.parentUrl !== null) {
        let op = $('<div class="col col-lg-10">')
            .append('<a class="card-subtitle text-muted" href="' + chirick.parentUrl + '">Original Post</a>');
        console.log($(op));
        userRow.append(op);
    }


    let userCol = $('<div class="col col-lg-10">');
    let userName = $("<h5>")
        .text(chirick.userName);
    let userHandle = $('<h6 class="card-subtitle text-muted">')
        .text('@' + chirick.userHandle);

    let chirickContent = $('<div class="row mt-3">')
        .append($('<div class="col col-lg-10">')
            .append($('<a class="link-text" href="/@' + chirick.userHandle + '/' + chirick.id + '">')
                .append($("<p id=\"chirick\">")
                    .text(chirick.chirick))));

    let actionsRow = $('<div class="row" id="actions">');
    let actionsCol = $('<div class="col col-lg-10">');
    let actionsSize = $('<h5>');
    let rechiricks = $('<span class="fake-link link-color link-color-green" title="Rechirick">')
        .text('\u21C6 ' + (chirick.rechiricksSize > 0 ? chirick.rechiricksSize : ""))
        .addClass(chirick.rechiricked ? "link-color-green-active" : "")
        .click((event) => {
            rechirick(event.target);
        });

    let likes = $('<span class="fake-link link-color ml-3 link-color-red" title="Like">')
        .text('\u2764 ' + (chirick.likesSize > 0 ? chirick.likesSize : ""))
        .addClass(chirick.liked ? "link-color-red-active" : "")
        .click((event) => {
            like(event.target);
        });

    let commentModal = $(`<div class="modal fade" id="commentModal` + index + `" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="col-11 modal-title text-center" id="exampleModalLongTitle">Comment on chirick</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      
      <div class="modal-body">
        <div class="row">
            <div class="col col-lg-12 form-green">
                <form class="form mt-3">
                    <div class="form-group">
                        <textarea class="form-control" id="chirickField` + index + `" style="resize: none" placeholder="Comment on chirick!" rows="3" name="comment"></textarea>
                    </div>
                    
                    <div class="d-flex">
                        <div class="ml-auto">
                            <span class="text-muted mr-3" id="charCounter` + index + `">0</span>
                            <button type="button" class="btn btn-outline-success mb-2" id="commentBtn" data-dismiss="modal" data-id="` + chirick.id + `">Comment</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
      </div>
    </div>
  </div>
</div>`);


    let charCounter = commentModal.find("#charCounter" + index);
    let chirickField = commentModal.find("#chirickField" + index);
    $(chirickField).bind('input propertychange', () => {
        charCounter.text(chirickField.val().length)
    });

    commentModal.find("button#commentBtn").click((event) => {
        comment(event.target);
    });

    let comments = $('<span class="fake-link link-color ml-3 link-color-blue" title="Comment" data-toggle="modal" data-target="#commentModal' + index + '">')
        .text('\uD83D\uDCAC ' + (chirick.commentsSize > 0 ? chirick.commentsSize : ""))
        .addClass(chirick.commented ? "link-color-blue-active" : "");

    userCol
        .append(userName)
        .append(userHandle);

    userRow
        .append(userCol);

    actionsSize
        .append(rechiricks)
        .append(likes)
        .append(comments);


    actionsCol
        .append(actionsSize);

    actionsRow
        .append(actionsCol);

    mainCol
        .append(userRow)
        .append(chirickContent);

    mainRow
        .append(imageCol)
        .append(mainCol);

    let modalBody = $(commentModal
        .find('div.modal-body'));
    modalBody.prepend(mainRow
        .clone()
        .removeAttr("id"));

    mainCol.append(actionsRow);
    mainRow
        .append($('<p hidden="hidden" id="id">')
            .text(chirick.id))
        .append(commentModal);

    content
        .append(mainRow)
        .append('<hr class="my-1 mt-3 mb-2" />');

}