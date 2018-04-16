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
            $('#alert').append(createAlert("alert-success", message));
        },
        error: function (data) {
            $('#alert').append(createAlert("alert-danger", data.responseText));
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
    if (!loader.length) {
        loader = $('<div class="d-flex justify-content-center" id="loader">')
                .append($('<div class="loader">'));
    }

    contentHolder.append(loader);

    $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: '/chirick/' + handle + '/' + activityType + '?page=' + page().getNext(),
        error: () => {
            loader.remove();
            $('#alert')
                .append(createAlert("alert-danger", "Chiricks could not be loaded, refresh the page and try again."))
        },
        success: (data) => {
            loader.remove();

            if (data.length < page().getMaxPerPage()) {
                page().hasNext(false);
            }

            let chiricksCount = $(".modal").length;
            for (let i = 0; i < data.length; i++) {
                let chirick = data[i];
                let div = createChirickDiv(chirick, chiricksCount + i);
                contentHolder.append(div).append('<hr class="my-1 mt-3 mb-2" />');
            }
        }
    });
}

function getRelationshipsForUser(relationshipType) {
    let contentHolder = $("#content");

    let loader = $("#loader");
    if (!loader.length) {
        loader = $('<div class="d-flex justify-content-center" id="loader">')
                .append($('<div class="loader">'));
    }

    contentHolder.append(loader);

    let handle  = $("#userCardHandle").text();

    $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: '/user/' + handle + '/' + relationshipType + '?page=' + page().getNext(),
        success: (data) => {
            console.log(data);
            loader.remove();

            if (data.length < page().getMaxPerPage()) {
                page().hasNext(false);
            }

            for (let i = 0; i < data.length; i++) {
                let user = data[i];
                createFollowDiv(user, contentHolder);
            }
        },
        error: () => {
            loader.remove();
            $('#alert')
                .append(createAlert("alert-danger", "Followers could not be loaded, refresh the page and try again."))
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
        url: '/chirick/comments/' + postId + '?page=' + page().getNext(),
        success: (data) => {
            loader.remove();

            if (data.length < page().getMaxPerPage()) {
                page().hasNext(false);
            }

            let chiricksCount = $(".modal").length;
            for (let i = 0; i < data.length; i++) {
                let chirick = data[i];
                let div = createChirickDiv(chirick, chiricksCount + i);
                contentHolder.append(div).append('<hr class="my-1 mt-3 mb-2" />');
            }
        },
        error: () => {
            loader.remove();
            $('#alert')
                .prepend(createAlert("alert-danger", "Comments could not be loaded, refresh the page and try again."))
        }
    });
}

function getFollowersForUser() {
    getRelationshipsForUser("followers");
}

function getFollowingForUser() {
    getRelationshipsForUser("following");
}

function getTimeline() {
    let contentHolder = $("#content");

    let loader = $("#loader");
    if (!loader.length) loader =
        $('<div class="d-flex justify-content-center" id="loader">')
            .append($('<div class="loader">'));

    contentHolder.append(loader);

    $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: '/timeline?page=' + page().getNext(),
        error: () => {
            loader.remove();
            $('#alert')
                .append(createAlert("alert-danger", "Chiricks could not be loaded, refresh the page and try again."))
        },
        success: (data) => {
            loader.remove();

            if (data.length < page().getMaxPerPage()) {
                page().hasNext(false);
            }

            let chiricksCount = $(".modal").length;
            for (let i = 0; i < data.length; i++) {
                let timelinePost = data[i];
                let chirick = timelinePost.chirick;

                let div = createChirickDiv(chirick, chiricksCount + i);
                contentHolder.append(div).append('<hr class="my-1 mt-3 mb-2" />');

                let userRow = div.find('#userRow');
                if (timelinePost.postTypeValue !== '') {
                    let activity = $('<div class="col col-lg-10">')
                        .append('<small class="card-subtitle text-muted">@' + timelinePost.userHandle + " " + timelinePost.postTypeValue + '</small>');
                    $(userRow).prepend(activity);
                }
            }
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
            $('#alert').append(createAlert("alert-danger", error.message));
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
            $('#alert').append(
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

    $.ajax({
        type: 'POST',
        url: '/chirick/comment',
        data: data,
        headers: headers,
        success: (result) => {
            let contentHolder = $("#content");
            let mainDiv = $(form).parent().parent().parent().parent().parent().parent().parent();
            let commentIcon = $(mainDiv)
                .find(".link-color-blue");

            commentIcon
                .addClass("link-color-blue-active")
                .text("\uD83D\uDCAC " + (result.commentsSize > 0 ? result.commentsSize : ""));

            contentHolder.prepend(createAlert("alert-success", "Successfully commented!"))
        },
        error: (error) => {
            let errorMessage = error.responseText;
            $('#alert').append(createAlert("alert-danger", errorMessage));
        }
    });
}

function follow(target) {
    let targetElement = $(target);
    let handle = targetElement.attr('data-handle');

    let token = $('meta[name="_csrf"]').attr("content");
    let headerName = $('meta[name="_csrf_headerName"]').attr("content");

    let headers = {};
    headers["_csrf"] = token;
    headers["_csrf_headerName"] = headerName;

    let data = {};
    data['handle'] = handle;
    data['_csrf'] = token;

    $.ajax({
        type: 'POST',
        url: '/user/follow',
        data: data,
        headers: headers,
        error: () => {
            let contentHolder = $('#alert');
            contentHolder
                .append(createAlert("alert-danger", "Could not follow user! Please try again!"));
        },
        success: (result) => {
            if (result.unfollowed) {
                targetElement
                    .removeClass('btn-success')
                    .addClass('btn-outline-success')
                    .text('Follow');
            } else {
                targetElement
                    .addClass('btn-success')
                    .removeClass('btn-outline-success')
                    .text('Following');
            }
        }
    });
}


function createChirickDiv(chirick, index) {
    let mainRow = $('<div class=\"row\" id="mainRow">');

    let imageCol = $('<div class=\"col col-3 col-sm-2 col-md-2 col-lg-2\">')
        .append('<img class="img-fluid rounded-circle post-image-small" alt="Profile image" src="' + chirick.userProfilePicUrl + '"/>');

    let mainCol = $('<div class="col col-12 col-sm-10 col-md-10 col-lg-10">');

    let userRow = $('<div class="row" id="userRow">');
    if (chirick.parentUrl !== null) {
        let op = $('<div class="col col-md-9 col-lg-9">')
            .append('<a class="card-subtitle text-muted" href="' + chirick.parentUrl + '">Comment to</a>');
        userRow.append(op);
    }


    let userCol = $('<div class="col col-12 col-sm-10 col-md-9 col-lg-9">');
    let userName = $("<h5>")
        .text(chirick.userName);
    let userHandle = $('<h6 class="card-subtitle">')
        .append($('<a class="text-muted">')
            .attr("href", "/@" + chirick.userHandle)
            .text('@' + chirick.userHandle));

    let chirickContent = $('<div class="row mt-3">')
        .append($('<div class="col col-md-9 col-lg-9">')
            .append($('<a class="link-text" href="/@' + chirick.userHandle + '/' + chirick.id + '">')
                .append($("<p id=\"chirick\">")
                    .text(chirick.chirick))));

    let actionsRow = $('<div class="row" id="actions">');
    let actionsCol = $('<div class="col col-md-9 col-lg-9">');
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

    return mainRow;
}

function createFollowDiv(user, contentHolder) {
    let card = $('<div class="col col-12 col-sm-6 col-md-6 col-lg-6 mb-4 w-100"><div class="card border-light h-100" ><div class="card-body">');

    let mainCol = $('<div class="col">');

    let userRow = $('<div class="row">');

    let userCol = $('<div class="col mt-1">');
    let userName = $("<h5>")
        .text(user.name);
    let userHandle = $('<h6 class="card-subtitle">')
        .append($('<a class="text-muted">')
            .attr("href", "/@" + user.handle)
            .text('@' + user.handle));

    let followBtn = $('<span class="fake-link btn btn-sm col mt-1">')
                .text(user.followed ? 'Following' : 'Follow')
                .addClass(user.followed ? 'btn-success' : 'btn-outline-success')
                .attr("data-handle", user.handle)
                .click((event) => {
                    follow(event.target);
                });

    let pictureCol = $('<div class="col col-6 col-sm-6 col-md-6 col-lg-6">')
        .append($('<img class="img-fluid rounded-circle min-user-image" alt="Profile picture" src="' + user.profilePicUrl + '"/>'));

    let bioRow = $('<div class="row">')
        .append($('<div class="col">')
            .append($('<p>').text(user.profileBiography)));

    if (!user.self) {
        userCol.append(followBtn);
    }

    userCol
        .append(userName)
        .append(userHandle);

    userRow
        .append(pictureCol)
        .append(userCol);

    mainCol
        .append(userRow)
        .append(bioRow);

    card.find('.card-body').append(mainCol);

    contentHolder.append(card);
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

function initializeActivityTabs(contentHolder) {
    let chirickTab = $("#chiricksTab");
    let rechirickTab = $("#rechiricksTab");
    let commentTab = $("#commentsTab");
    let likeTab = $("#likesTab");

    chirickTab.click(() => {
        if (chirickTab.hasClass("active")) return;

        contentHolder.html("");
        $(chirickTab).addClass("active");
        $(rechirickTab).removeClass("active");
        $(commentTab).removeClass("active");
        $(likeTab).removeClass("active");
        page().reset();
        page().setLoadingFunction(getChiricksForUser);
        page().execLoadFunction();
    });
    rechirickTab.click(() => {
        if (rechirickTab.hasClass("active")) return;

        contentHolder.html("");
        $(chirickTab).removeClass("active");
        $(rechirickTab).addClass("active");
        $(commentTab).removeClass("active");
        $(likeTab).removeClass("active");
        page().reset();
        page().setLoadingFunction(getRechiricksForUser);
        page().execLoadFunction();
    });
    commentTab.click(() => {
        if (commentTab.hasClass("active")) return;

        contentHolder.html("");
        chirickTab.removeClass("active");
        rechirickTab.removeClass("active");
        commentTab.addClass("active");
        likeTab.removeClass("active");
        page().reset();
        page().setLoadingFunction(getCommentsForUser);
        page().execLoadFunction();
    });

    likeTab.click(() => {
        if (likeTab.hasClass("active")) return;

        contentHolder.html("");
        chirickTab.removeClass("active");
        rechirickTab.removeClass("active");
        commentTab.removeClass("active");
        likeTab.addClass("active");
        page().reset();
        page().setLoadingFunction(getLikesForUser);
        page().execLoadFunction();
    });
}

function isFileValid() {
    const supportedTypes = ['image/jpeg', 'image/png', 'image/bmp'];

    let file = $("#profilePicture")[0].files[0];
    let fileSize = file.size;
    let isFileTypeSupported = $.inArray(file.type, supportedTypes) !== -1;

    if (fileSize < 1000000 && isFileTypeSupported) return true;

    let pictureDiv = $("#picture");

    let errorDiv = $("#picError");
    if (!errorDiv.length) {
        errorDiv = $("<div id=\"picError\">");
        pictureDiv.append(errorDiv);
    } else {
        errorDiv.html("");
    }

    if (fileSize > 1000000) {
        let sizeError = $("#sizeError");
        if (!sizeError.length) {
            sizeError = $("<p class=\"text-danger mb-0\" id=\"sizeError\">");
            errorDiv.append(sizeError);
        }
        sizeError.text("File size cannot be more than 1MB. ");
    }

    if (!isFileTypeSupported) {
        console.log(fileSize);
        let typeError = $("#typeError");
        if (!typeError.length) {
            typeError = $("<p class=\"text-danger mb-0\" id=\"typeError\">");
            errorDiv.append(typeError);
        }
        typeError.text("File type has to be jpg, jpeg, png or bmp. ");
    }

    return false;
}