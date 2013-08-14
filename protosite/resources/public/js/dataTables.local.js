function loadDataTable(url){
    $.ajax({
        url: url,
        success: function(data, aStatus, dummy){
            oTable = $('#example').dataTable({
                "bProcessing": true,
            "aoColumns": data.columns,
            "aaData": data.data,
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
            oTable.fnAddData(data.data);
        },
    dataType: "json"});
}
