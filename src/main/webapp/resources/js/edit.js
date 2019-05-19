$(function () {
    $('.updateData').click(function () {
        console.log('updateData')
        var id = $(this).attr('id').substring(7);
        $('#fname_' + id).hide();
        $('#text_fname_' + id).show();
        $('#text_fname_' + id).focus();
        $('#lname_' + id).hide();
        $('#text_lname_' + id).show();
        $(this).hide();
        $('#save_' + id).show();

    });

    $('.updateAreaData').click(function () {
    	console.info($(this).attr('id'));
        var id = $(this).attr('id').substring(7);
        $('#name_' + id).hide();
        $('#text_name_' + id).show();
        $('#text_name_' + id).focus();
        $('#name2_' + id).hide();
        $('#text_name2_' + id).show();
        $('#areaCode_' + id).hide();
        $('#text_areaCode_' + id).show();
        $('#lcoCode_' + id).hide();
        $('#text_lcoCode_' + id).show();
        $('#lcoName_' + id).hide();
        $('#text_lcoName_' + id).show();
        $(this).hide();
        $('#saveArea_' + id).show();

    });

    
});