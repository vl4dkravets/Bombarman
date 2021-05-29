var ClusterSetting = function () {

//    this.gameServer = {
//        protocol: 'ws',
//        host: 'localhost',
//        port: '8080',
//        path: '/events/connect'
//    };
//
//    this.matchMaker = {
//        protocol: 'http',
//        host: 'localhost',
//        port: '8080',
//        path: '/matchmaker/join'
//    };

    this.gameServer = {
        protocol: 'wss',
        host: 'bomberman-by-vlad.herokuapp.com',
        port: '',
        path: '/events/connect'
    };

    this.matchMaker = {
        protocol: 'https',
        host: 'bomberman-by-vlad.herokuapp.com',
        port: '',
        path: '/matchmaker/join'
    };

};

ClusterSetting.prototype.gameServerUrl = function() {
    return makeUrl(this.gameServer)
};

ClusterSetting.prototype.matchMakerUrl = function() {
    return makeUrl(this.matchMaker)
};

function makeUrl(data) {
    return data['protocol'] + "://" + data['host'] + ":" + data['port'] + data['path']
}

var gClusterSettings = new ClusterSetting();
