'use strict';

testus.service('releaseWizardSvc', function ($rootScope, localStorage) {

    var LOCAL_STORAGE_RELEASE = 'fmRelease',
        releaseString = localStorage[LOCAL_STORAGE_RELEASE];

    var LOCAL_STORAGE_SELECTION = 'fmSelection',
        selectionString = localStorage[LOCAL_STORAGE_SELECTION];

    var LOCAL_STORAGE_STORIES = 'fmStories',
        storiesString = localStorage[LOCAL_STORAGE_STORIES];

    var LOCAL_STORAGE_BROWSERS = 'fmBrowsers',
        browsersString = localStorage[LOCAL_STORAGE_BROWSERS];

    var LOCAL_STORAGE_LANGS = 'fmLangs',
        langsString = localStorage[LOCAL_STORAGE_LANGS];

    var selBrowsers = browsersString ? JSON.parse(browsersString) : {};
    var selLangs = langsString ? JSON.parse(langsString) : {};
    var selStories = storiesString ? JSON.parse(storiesString) : {};

    var selection = selectionString ? JSON.parse(selectionString) : [];
    var selectedStories = {};// storiesString ? JSON.parse(storiesString) : {};
    var release = releaseString ? JSON.parse(releaseString) : {};
    var campaigns = [];

    $rootScope.$watch(function () {
        return selection;
    }, function () {
        console.log("watch selection", selection);
        localStorage[LOCAL_STORAGE_SELECTION] = JSON.stringify(selection);
    }, true);

    $rootScope.$watch(function () {
        return release;
    }, function () {
        console.log("watch release", release);
        localStorage[LOCAL_STORAGE_RELEASE] = JSON.stringify(release);
    }, true);

    $rootScope.$watch(function () {
        return selStories;
    }, function () {
        console.log("watch selStories", selStories);
        localStorage[LOCAL_STORAGE_STORIES] = JSON.stringify(selStories);
    }, true);

    $rootScope.$watch(function () {
        return selBrowsers;
    }, function () {
        console.log("watch selBrowsers", selBrowsers);
        localStorage[LOCAL_STORAGE_BROWSERS] = JSON.stringify(selBrowsers);
    }, true);

    $rootScope.$watch(function () {
        return selLangs;
    }, function () {
        console.log("watch selLangs", selLangs);
        localStorage[LOCAL_STORAGE_LANGS] = JSON.stringify(selLangs);
    }, true);


    return {
        getSelection: function () {
            return selection;
        },
        setSelection: function (sel) {
            selection = sel;
            console.log('setSelection', selection);

            angular.forEach(selection, function (item) {

                var browser = {};
                var language = {};
                angular.forEach(item.browsers, function (key, value) {
                    browser = value;
                    angular.forEach(item.languages, function (key, value) {
                        language = value;

                        campaigns.push({
                            story: item.story,
                            storyId: item.story.id,
                            browser: browser,
                            language: language,
                            name: release.name + " - " + item.story.name + " - " + browser + " - " + language
                        });
                    });
                });
            });
            console.log(campaigns);

        },
        setSelectedStories: function (stories) {
            selectedStories = stories;
            selection = {};
            angular.forEach(selectedStories, function (story) {
                selection[story.id] = {
                    story: story,
                    browsers: {},
                    languages: {}
                };
            });
            console.log("setSelectedStories", selection);
        },
        getRelease: function () {
            return release;
        },
        setRelease: function (rel) {
            release = rel;
            console.log('setRelease', release);
        },
        submit: function () {
            console.log("submit");
            //TODO add alert on success & errors
            jsRoutes.controllers.Releases.save().ajax({
                data: JSON.stringify(release),
                contentType: "application/json",
                dataType: "json",
                success: function (data, status) {
                    console.log("success!", data, status);

                    $.pnotify({
                        title: 'Release created',
                        text: 'The release have been successfully created',
                        type: 'success',
                        icon: 'picon picon-flag-green'
                    });

                    var id = data;
                    var c = [];
                    angular.forEach(campaigns, function (campaign) {
                        c.push({
                            storyId: campaign.storyId,
                            browser: campaign.browser,
                            name: campaign.name,
                            lang: campaign.language,
                            campaignId: id
                        });
                    });
                    jsRoutes.controllers.Releases.genCampaigns().ajax({
                        data: JSON.stringify(c),
                        contentType: "application/json",
                        dataType: "json",
                        success: function (data, status) {
                            console.log("success!", data, status);
                            //resetStorage();
                            $.pnotify({
                                title: 'Campaigns created',
                                text: 'The campaigns have been successfully created',
                                type: 'success'
                            });
                        },
                        error: function (data, status) {
                            console.log("Failed!", data, status);
                            //$scope.data = data || "Request failed";
                            $.pnotify({
                                title: 'Oh No!',
                                text: 'Something terrible happened while creating campaigns.',
                                type: 'error'
                            });
                        }
                    });
                },
                error: function (data, status) {
                    console.log("Failed!", data, status);
                    //$scope.data = data || "Request failed";
                    $.pnotify({
                        title: 'Oh No!',
                        text: 'Something terrible happened while creating the release.',
                        type: 'error'
                    });
                }
            });

        },
        getCampaigns: function () {
            return campaigns;
        },
        getBrowsers: function () {
            return selBrowsers;
        },
        getLangs: function () {
            return selLangs;
        },
        getStories: function () {
            return selStories;
        },
        resetStorage: function () {
            localStorage.removeItem(LOCAL_STORAGE_SELECTION);
            localStorage.removeItem(LOCAL_STORAGE_RELEASE);
            localStorage.removeItem(LOCAL_STORAGE_BROWSERS);
            localStorage.removeItem(LOCAL_STORAGE_LANGS);
            localStorage.removeItem(LOCAL_STORAGE_STORIES);
        }

    };
});