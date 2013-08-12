function urlFrom(team, year){
    return "/team/" + team + "/" + year + "/";
}

function loadDataTable(team, year){
    $.ajax({
        url: urlFrom(team, year),
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

function updateDataTable(tbl, team, year){
    var team = teamMap[team];
    $.ajax({
        url: urlFrom(team, year),
        success: function(data, aStatus, dummy){
            oTable.fnClearTable();
            oTable.fnAddData(data.aaData);
        },
    dataType: "json"});
}


$( document ).ready( function() {

    loadDataTable("NYN", 1990);

    $("#year").bind('keypress', function(e) {
        var year = $("#year").val() + String.fromCharCode(e.keyCode);
        var team = $("#team").val();
        if(year.length == 4){
            updateDataTable(oTable, team, year);
        }
    });

    $("#team").bind('keypress', function(e) {
        var team = $("#team").val() + String.fromCharCode(e.keyCode);
        var year = $("#year").val() ;
        if(_.contains(teamNames, team)){
            updateDataTable(oTable, team, year);
        }
    });

    // Set up auto-complete
    $.ajax({
        url: "/teamnames/",
        success: function(data, aStatus, dummy){
            teamMap = data;
            teamNames = _.keys(data);
            $( "#team" ).autocomplete({
                source: teamNames
            });
        },
        dataType: "json"});
});
