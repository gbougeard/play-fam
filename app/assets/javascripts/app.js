/**
 * The app module, as both AngularJS as well as RequireJS module.
 * Splitting an app in several Angular modules serves no real purpose in Angular 1.0/1.1.
 * (Hopefully this will change in the near future.)
 * Splitting it into several RequireJS modules allows async loading. We cannot take full advantage
 * of RequireJS and lazy-load stuff because the angular modules have their own dependency system.
 */
define(["angular", "angular-ui", "angular-strap", "place", "event"], function(angular) {
    // We must already declare most dependencies here (except for common), or the submodules' routes
    // will not be resolved
    var mod =  angular.module("app", ["ui", "$strap.directives", "fam.place", "fam.event"]);
//    mod.config(function(RestangularProvider) {
//        RestangularProvider.setBaseUrl("/api/v1");
//    });
    return mod;
});