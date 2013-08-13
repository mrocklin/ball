function urlFrom(team, year){
    return "/team/" + team + "/" + year + "/";
}

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


$( document ).ready( function() {

    loadDataTable(urlFrom("NYN", 1990));

    $("#year").bind('keypress', function(e) {
        var year = $("#year").val() + String.fromCharCode(e.keyCode);
        var teamName = $("#team").val();
        var team = teamMap[teamName];
        if(year.length == 4){
            updateDataTable(oTable, urlFrom(team, year));
        }
    });

    $("#team").bind('keypress', function(e) {
        var teamName = $("#team").val() + String.fromCharCode(e.keyCode);
        var team = teamMap[teamName];
        var year = $("#year").val() ;
        if(_.contains(teamNames, team)){
            updateDataTable(oTable, urlFrom(team, year));
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
