function urlFrom(batter){
    return "/player/" + batter + "/";
}

function loadDataTable(url){
    $.ajax({
        url: url,
        success: function(data, aStatus, dummy){
            console.log(data);
            oTable = $('#example').dataTable({
                "bProcessing": true,
            "aoColumns": data.aoColumns,
            "aaData": data.aaData,
            "iDisplayLength": 25,
            });
        },
        dataType: "json"});
}

function updateDataTable(tbl, url){
    $.ajax({
        url: url,
        success: function(data, aStatus, dummy){
            oTable.fnClearTable();
            oTable.fnAddData(data.aaData);
        },
    dataType: "json"});
}


$( document ).ready( function() {

    loadDataTable(urlFrom("strawda01"));

    $("#batter").bind('keypress', function(e) {
        var batter = $("#batter").val() + String.fromCharCode(e.keyCode);
        updateDataTable(oTable, urlFrom(batter));
    });
});
