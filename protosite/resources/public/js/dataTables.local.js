function loadDataTable(url){
    $.ajax({
        url: url,
        success: function(data, aStatus, dummy){
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
