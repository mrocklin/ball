function urlFrom(team, year){
    return "/team/" + team + "/" + year + "/";
}


function setUpClickAction(){
    // Set up click action
    $(document).on("click", ".clickableValue", function() {
        var playerID = $(this).parent().parent().attr('id');
        var attribute = $(this).parent().attr('id');
        var year = $("#year").val() ;
        $.ajax({
            url: "/player-history/"+playerID+"/"+attribute+"/",
            success: function(data, sStatus, dummy){
                        $("#playerdata").html(data.data.join(','));
                     },
            dataType: "json"
            });
        $.ajax({
            url: "/year-attribute/"+year+"/"+attribute+"/",
            dataType: "json",
            success: function(data, sStatus, dummy){
                        var bars = bins(data.data, 20);
                        $("#yeardata").html(
                        "BarCenters: "+bars.centers.join(', ') +
                        "    BarHeights: "+bars.heights.join(', '));
                        tmp = data.data;
                     }
        });
    });
}

$( document ).ready( function() {

    loadDataTable(urlFrom("SFN", 2012));

    setUpClickAction();

    $("#year").bind('keypress', function(e) {
        var year = $("#year").val() + String.fromCharCode(e.keyCode);
        var teamName = $("#team").val();
        var team = teamMap[teamName];
        if(year.length == 4){
            updateDataTable(oTable, urlFrom(team, year), [setUpClickAction]);
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
