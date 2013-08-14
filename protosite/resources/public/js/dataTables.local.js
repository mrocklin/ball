function processData(data){
    data = addPlayerIDLinks(data);
    return data;
}

function loadDataTable(url){
    $.ajax({
        url: url,
        success: function(data, aStatus, dummy){
            data = processData(data);
            Data = data;
            oTable = $('#example').dataTable({
                "bProcessing": true,
            "aoColumns": addTitleToColumns(data.columns),
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
            oTable.fnAddData(processData(data.data));
        },
    dataType: "json"});
}

function addTitleToColumns(columns){
    return _.map(columns, function(c){return {sTitle: c};});
}

// Operates in place
function addPlayerIDLinks(data){
    if (_.contains(data.columns, "playerID")){
        var idx = data.columns.indexOf("playerID");
        for(var r = 0; r < data.data.length; r++){
            var pid = data.data[r][idx];
            var val = data.data[r][0];
            data.data[r][0] = "<a href=\"/player/"+pid+"/\">"+val+"</a>";
        }
    }
    return data;
}

function removeIdx(idx, arr)
{
    return arr.slice(0, idx).concat(arr.slice(idx+1, -1));
}

function removePlayerID(data){
    var idx = data.columns.indexOf("playerID");
    if (idx == -1)
        return data;
    var removePlayerID = _.partial(removeIdx, idx);
    return {columns: removePlayerId(data.columns),
            rows: data.rows,
            data: _.map(data.data, removePlayerID)};
}

