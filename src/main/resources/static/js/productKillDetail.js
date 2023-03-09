$(function(){
    //给type绑定点击事件
    $(".type").click(function () {
        var index = $(".type").index(this);
        var obj = $(".type").eq(index);
        $(".type").removeClass("checked");
        obj.addClass("checked");
    });
    //给color绑定点击事件
    $(".color").click(function () {
        var index = $(".color").index(this);
        var obj = $(".color").eq(index);
        $(".color").removeClass("checked");
        obj.addClass("checked");
    });
    countDown();
});

// function increase(){
//     let quantity = $("#quantity").val();
//     let stockStr = $("#stock").text();
//     let stock = parseInt(stockStr);
//     if(quantity < stock){
//         quantity++;
//     }
//     $("#quantity").val(quantity);
// }
//
// function reduce() {
//     let quantity = $("#quantity").val();
//     if(quantity > 1){
//         quantity--;
//     }
//     $("#quantity").val(quantity);
// }
//
// //添加购物车
// function addCart(productId,price){
//     let stockStr = $("#stock").text();
//     let stock = parseInt(stockStr);
//     if(stock == 0){
//         alert("库存不足！");
//         return false;
//     }
//     let quantity = $("#quantity").val();
//     window.location.href="/cart/add/"+productId+"/"+price+"/"+quantity;
// }

function countDown() {
    var remainSeconds = $("#remainSeconds").val();
    var timeout;
    if (remainSeconds>0){
        // $("#buyButton").attr("disabled",true);
        document.getElementById("buyButton").style.backgroundColor="#CDCDCD";
        timeout = setTimeout(function () {
            $("#countDown").text(remainSeconds-1);
            $("#remainSeconds").val(remainSeconds-1);
            countDown();
        },1000);
    } else if (remainSeconds==0){
        // $("#buyButton").attr("disabled",false);
        document.getElementById("buyButton").style.backgroundColor="red";
        if (timeout){
            clearTimeout(timeout);
        }
        $("#seckillTip").html("秒杀进行中");
    }else {
        // $("#buyButton").attr("disabled",true);
        document.getElementById("buyButton").style.backgroundColor="#CDCDCD";
        $("#seckillTip").html("秒杀已经结束");
    }
}

function doSeckill() {
    $.ajax({
        url:'/seckill/doSeckill',
        type:'POST',
        data:{
            productId:$("#productId").val()
        },
        success:function (data) {
            if(data.code==200){
                getResult($("#productId").val());
            }else {
                layer.msg(data.message);
            }
        },
        error:function () {
            layer.msg("客户端请求错误");
        }
    })
}
function getResult(productId) {
    g_showLoading();
    $.ajax({
        url:"/seckill/result",
        type:"GET",
        data:{
            productId:productId,
        },
        success:function (data) {
            if (data.code==200){
                var result = data.obj;
                if(result<0){
                    layer.msg("对不起，秒杀失败!");
                }else if(result==0){
                    setTimeout(function () {
                        getResult(productId);
                    },50);
                }else {
                    window.location.href="/orderDetail.htm?orderId="+result;
                }
            }
        },
        error:function () {
            layer.msg("客户端请求错误");
        }
    })
}