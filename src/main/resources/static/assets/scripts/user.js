function getUserCard() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/user/card",
        success: function(data) {
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
        success: function(data) {
            $("#profileImg").attr("src", data.profilePicUrl);
            $("#profileName").text(data.name);
            $("#profileHandle").text("@" + data.handle);
            $("#profileLink").attr("href", "/" + data.handle);
        }
    });
}