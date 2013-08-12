



$( document ).ready( function() {

    // Load Data Table
    $.ajax({
        url: "/team/NYN/1990/",
        success: function(data, aStatus, dummy){
            oTable = $('#example').dataTable({
                "bProcessing": true,
            "aoColumns": data.aoColumns,
            "aaData": data.aaData,
            "iDisplayLength": 25,
            });
        },
        dataType: "json"});


    function updateDataTable(tbl, team, year){
        team = teamMap[team];
        var url = "/team/" + team + "/" + year + "/";
        $.ajax({
            url: url,
            success: function(data, aStatus, dummy){
                oTable.fnClearTable();
                oTable.fnAddData(data.aaData);
            },
        dataType: "json"});
    }

    $("#year").bind('keypress', function(e) {
        var year = $("#year").val() + String.fromCharCode(e.keyCode);
        var team = $("#team").val()
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
