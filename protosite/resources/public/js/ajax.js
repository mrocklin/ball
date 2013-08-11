



$( document ).ready( function() {

    oTable = $('#example').dataTable({
        "bProcessing": true,
           "sAjaxSource": "/team/NYN/1990/",
           "iDisplayLength": 25,
           "fnServerData": function ( sUrl, aoData, fnCallback, oSettings ) {
               oSettings.jqXHR = $.ajax( {
                   "url":  sUrl,
               "data": aoData,
               "success": function (json) {
                   if ( json.sError ) {
                       oSettings.oApi._fnLog( oSettings, 0, json.sError );
                   }

                   $(oSettings.oInstance).trigger('xhr', [oSettings, json]);
                   fnCallback( json );
               },
               "dataType": "json",
               "cache": true,
               "type": "GET",
               "error": function (xhr, error, thrown) {
                   if ( error == "parsererror" ) {
                       oSettings.oApi._fnLog( oSettings, 0, "DataTables warning: JSON data from "+
                           "server could not be parsed. This is caused by a JSON formatting error." );
                   }
               }
               } );
           },
    });

    function updateDataTable(tbl, team, year){
        team = teamMap[team];
        var url = "/team/" + team + "/" + year + "/";
        tbl.fnReloadAjax(url);
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
        if(contains(teamNames, team)){
            updateDataTable(oTable, team, year);
        }
    });

    $.ajax({
        url: "/teamnames/",
        success: function(data, aStatus, dummy){
            teamMap = data;
            teamNames = keys(data);
            $( "#team" ).autocomplete({
                source: teamNames
            });
        },
        dataType: "json"});

});
