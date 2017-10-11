
$("body").append('<a id="returnTop"><i class="icon icon-arr-top"></i></a>'), $(window).scroll(function() {

}), $("#returnTop").on("click", function() {
	$(window).scrollTop(0)
}), $("#headers").load("header.html"), $("#footer").load("footer.html");