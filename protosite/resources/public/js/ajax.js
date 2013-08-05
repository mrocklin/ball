



$( document ).ready( function() {

    oTable = $('#example').dataTable({
        "bProcessing": true,
        "sAjaxSource": "/team/NYN/1990/",
        "iDisplayLength": 25
    });
    console.log(oTable);

    function updateDataTable(tbl, team, year){
        var url = "/team/" + team + "/" + year + "/";
        console.log(url);
        tbl.fnReloadAjax(url);
    }

    $("#year").bind('keypress', function(e) {
        var val = $("#year").val() + String.fromCharCode(e.keyCode);
        console.log(val);
        if(val.length == 4){
            console.log("call update");
            updateDataTable(oTable, "NYN", val);
        }
    });
});

