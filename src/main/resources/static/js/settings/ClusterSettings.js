var ClusterSetting = function () {
    this.gameServer = {
        //protocol: 'ws',
        protocol: 'wss',
        //host: 'localhost',
        host: 'bomberman-by-vlad.herokuapp.com',
        //port: '8080',
        port: '',
        path: '/events/connect'
    };

    this.matchMaker = {
        //protocol: 'http',
        protocol: 'https',
       // host: 'localhost',
        host: 'bomberman-by-vlad.herokuapp.com',
        //port: '8080',
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
