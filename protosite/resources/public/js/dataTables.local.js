function processData(data){
    data = addPlayerIDLinks(data);
    data = histClickable(data);
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
            "iDisplayLength": 25
            });
        },
        dataType: "json"});
}

function updateDataTable(tbl, url){
    $.ajax({
        url: url,
        success: function(data, aStatus, dummy){
            data = processData(data);
            oTable.fnClearTable();
            oTable.fnAddData(data.data);
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

function histDiv(row, col, value){
    return ("<div class=\"row\" id=\""+row+"\">" +
            "<div class=\"col\" id=\""+col+"\">" +
            "<div class=\"clickableValue\">" +
            value +
            "</div></div></div>");
}


function histClickable(data){
    for(var r = 0; r < data.data.length; r++){
        for(var c = 0; c < data.data[0].length; c++){
            var row = data.rows[r];
            var col = data.columns[c];
            var value = data.data[r][c];
            data.data[r][c] = histDiv(row, col, value);
        }
    }
    return data;
}

function bins(arr, n){
    var binsize = (_.max(arr) - _.min(arr)) / n;
    var key = function(x) { 
        return Math.round(x / binsize) * binsize;
    };
    var counts = _.countBy(arr, key);
    return {centers: _.keys(counts), 
            heights: _.values(counts)};
}

