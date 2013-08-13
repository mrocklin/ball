function urlFrom(batter){
    return "/player/" + batter + "/";
}

$( document ).ready( function() {

    loadDataTable(urlFrom("strawda01"));

    $("#batter").bind('keypress', function(e) {
        var batter = $("#batter").val() + String.fromCharCode(e.keyCode);
        updateDataTable(oTable, urlFrom(batter));
    });
});
