package com.davidtoh.helloworld;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

import com.davidtoh.helloworld.core_activities.DrawRouteActivity;
import com.davidtoh.helloworld.utils.Loc;
import com.davidtoh.helloworld.utils.connection;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;


/**
 * Created by manansaraf on 03/01/15.
 * class to test the basic functions in BusStopStatistics Activity
 */
public class DrawRouteActivityTest extends ActivityInstrumentationTestCase2<DrawRouteActivity> {

    private DrawRouteActivity drawRouteActivity;

    public DrawRouteActivityTest() {
        super(DrawRouteActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra("vehicle_id", 0112);
        intent.putExtra("shape_id", "SILVER%20NO%EDGE");
        intent.putExtra("route_color","cccccc");
        setActivityIntent(intent);
        drawRouteActivity = getActivity();
    }


    public void testPreconditions() {
        assertNotNull(drawRouteActivity);
    }

    public void testMakeConnection() {
        String url = "https://developer.cumtd.com/api/v2.2/JSON/GetVehicle?key=a6030286b6ed4d609f2178e7cc5a17c9&vehicle_id=01112";
        connection connect = new connection(url);
        String result = connect.getJSON();
            assertTrue(result.contains("{\"method\":\"GetVehicle\",\"params\":{\"vehicle_id\":\"0112\"}}"));

    }
    public void testBuildVehicleJSON() {
        try {
            Loc point = drawRouteActivity.buildVehicleJSON("{\"time\":\"2015-04-09T14:21:19-05:00\",\"new_changeset\":true,\"status\":{\"code\":200,\"msg\":\"ok\"},\"rqst\":{\"method\":\"GetVehicle\",\"params\":{\"vehicle_id\":\"0112\"}},\"vehicles\":[{\"vehicle_id\":\"0112\",\"trip\":{\"trip_id\":\"[@7.0.41200832@][1][1238430123625]\\/43__I7UIMF\",\"trip_headsign\":\"NORTH - LINCOLN & KILLARNEY\",\"route_id\":\"ILLINI\",\"block_id\":\"I7UIMF\",\"direction\":\"North\",\"service_id\":\"I7UIMF\",\"shape_id\":\"22N ILLINI 10\"},\"location\":{\"lat\":40.105450,\"lon\":-88.229000},\"previous_stop_id\":\"ARYWRT:3\",\"next_stop_id\":\"IU:1\",\"origin_stop_id\":\"PAR:2\",\"destination_stop_id\":\"LNCLNKLRNY:1\",\"last_updated\":\"2015-04-09T14:20:48-05:00\"}]}");
            assertNotNull("point is null", point);
            assertEquals(40.10545,point.getLatitude());
            assertEquals(-88.229,point.getLongitude());
        } catch (IOException e) {
            Log.e("TEST_ERROR", e.getMessage());
        }
    }

    public void testBuildShapeJSON() {
        try {
            List<Loc> points = drawRouteActivity.buildShapeJSON("{\"time\":\"2015-04-09T14:29:58-05:00\",\"changeset_id\":\"B2D0DE20D2611F17EF9F3DF391796E27\",\"new_changeset\":true,\"status\":{\"code\":200,\"msg\":\"ok\"},\"rqst\":{\"method\":\"GetShape\",\"params\":{\"shape_id\":\"220N ILLINI 10\"}},\"shapes\":[{\"shape_dist_traveled\":2.6,\"shape_pt_lat\":40.099466,\"shape_pt_lon\":-88.220416,\"shape_pt_sequence\":1,\"stop_id\":\"PAR:2\"},{\"shape_dist_traveled\":207.9,\"shape_pt_lat\":40.099411,\"shape_pt_lon\":-88.222829,\"shape_pt_sequence\":62},{\"shape_dist_traveled\":330.2,\"shape_pt_lat\":40.100512,\"shape_pt_lon\":-88.222848,\"shape_pt_sequence\":84,\"stop_id\":\"PAMD:2\"},{\"shape_dist_traveled\":343.6,\"shape_pt_lat\":40.100600,\"shape_pt_lon\":-88.222846,\"shape_pt_sequence\":89},{\"shape_dist_traveled\":660.7,\"shape_pt_lat\":40.100626,\"shape_pt_lon\":-88.219116,\"shape_pt_sequence\":145},{\"shape_dist_traveled\":1052.9,\"shape_pt_lat\":40.104155,\"shape_pt_lon\":-88.219187,\"shape_pt_sequence\":190,\"stop_id\":\"LAR:2\"},{\"shape_dist_traveled\":1409.2,\"shape_pt_lat\":40.107240,\"shape_pt_lon\":-88.219239,\"shape_pt_sequence\":273,\"stop_id\":\"LNCLNOR:2\"},{\"shape_dist_traveled\":1624.7,\"shape_pt_lat\":40.109065,\"shape_pt_lon\":-88.219271,\"shape_pt_sequence\":347},{\"shape_dist_traveled\":1661.0,\"shape_pt_lat\":40.109065,\"shape_pt_lon\":-88.219700,\"shape_pt_sequence\":361,\"stop_id\":\"LNCLNIL:8\"},{\"shape_dist_traveled\":1874.6,\"shape_pt_lat\":40.109051,\"shape_pt_lon\":-88.222090,\"shape_pt_sequence\":435,\"stop_id\":\"ISR:2\"},{\"shape_dist_traveled\":2048.6,\"shape_pt_lat\":40.109036,\"shape_pt_lon\":-88.223905,\"shape_pt_sequence\":482},{\"shape_dist_traveled\":2159.2,\"shape_pt_lat\":40.108042,\"shape_pt_lon\":-88.223872,\"shape_pt_sequence\":497,\"stop_id\":\"CHEMLS:1\"},{\"shape_dist_traveled\":2390.0,\"shape_pt_lat\":40.106102,\"shape_pt_lon\":-88.223852,\"shape_pt_sequence\":531,\"stop_id\":\"GWNNV:4\"},{\"shape_dist_traveled\":2596.0,\"shape_pt_lat\":40.104374,\"shape_pt_lon\":-88.223820,\"shape_pt_sequence\":559,\"stop_id\":\"GWNGRG:4\"},{\"shape_dist_traveled\":2627.2,\"shape_pt_lat\":40.104193,\"shape_pt_lon\":-88.223812,\"shape_pt_sequence\":564},{\"shape_dist_traveled\":2776.0,\"shape_pt_lat\":40.104180,\"shape_pt_lon\":-88.225563,\"shape_pt_sequence\":590,\"stop_id\":\"GRGMUM:2\"},{\"shape_dist_traveled\":3047.6,\"shape_pt_lat\":40.104161,\"shape_pt_lon\":-88.228678,\"shape_pt_sequence\":645,\"stop_id\":\"GRGLIB:2\"},{\"shape_dist_traveled\":3466.8,\"shape_pt_lat\":40.104127,\"shape_pt_lon\":-88.233430,\"shape_pt_sequence\":727},{\"shape_dist_traveled\":3501.1,\"shape_pt_lat\":40.103818,\"shape_pt_lon\":-88.233426,\"shape_pt_sequence\":733,\"stop_id\":\"4THGRG:7\"},{\"shape_dist_traveled\":3759.7,\"shape_pt_lat\":40.101605,\"shape_pt_lon\":-88.233385,\"shape_pt_sequence\":777,\"stop_id\":\"4THPDY:4\"},{\"shape_dist_traveled\":3789.5,\"shape_pt_lat\":40.101464,\"shape_pt_lon\":-88.233384,\"shape_pt_sequence\":781},{\"shape_dist_traveled\":4016.6,\"shape_pt_lat\":40.101449,\"shape_pt_lon\":-88.236055,\"shape_pt_sequence\":835,\"stop_id\":\"ARC:2\"},{\"shape_dist_traveled\":4225.1,\"shape_pt_lat\":40.101439,\"shape_pt_lon\":-88.238410,\"shape_pt_sequence\":872,\"stop_id\":\"1STPDY:1\"},{\"shape_dist_traveled\":4247.9,\"shape_pt_lat\":40.101436,\"shape_pt_lon\":-88.238607,\"shape_pt_sequence\":877},{\"shape_dist_traveled\":4465.5,\"shape_pt_lat\":40.103394,\"shape_pt_lon\":-88.238629,\"shape_pt_sequence\":879,\"stop_id\":\"1STSTDM:5\"},{\"shape_dist_traveled\":4718.2,\"shape_pt_lat\":40.105579,\"shape_pt_lon\":-88.238646,\"shape_pt_sequence\":884,\"stop_id\":\"1STARY:5\"},{\"shape_dist_traveled\":4985.0,\"shape_pt_lat\":40.107906,\"shape_pt_lon\":-88.238672,\"shape_pt_sequence\":888},{\"shape_dist_traveled\":5012.7,\"shape_pt_lat\":40.107910,\"shape_pt_lon\":-88.238347,\"shape_pt_sequence\":894,\"stop_id\":\"1STDAN:6\"},{\"shape_dist_traveled\":5256.4,\"shape_pt_lat\":40.107936,\"shape_pt_lon\":-88.235562,\"shape_pt_sequence\":936,\"stop_id\":\"3RDDAN:3\"},{\"shape_dist_traveled\":5440.1,\"shape_pt_lat\":40.107950,\"shape_pt_lon\":-88.233500,\"shape_pt_sequence\":967},{\"shape_dist_traveled\":5574.2,\"shape_pt_lat\":40.106744,\"shape_pt_lon\":-88.233478,\"shape_pt_sequence\":984,\"stop_id\":\"4THCHAL:4\"},{\"shape_dist_traveled\":5727.6,\"shape_pt_lat\":40.105413,\"shape_pt_lon\":-88.233452,\"shape_pt_sequence\":1013},{\"shape_dist_traveled\":5809.4,\"shape_pt_lat\":40.105419,\"shape_pt_lon\":-88.232490,\"shape_pt_sequence\":1034,\"stop_id\":\"4THARY:6\"},{\"shape_dist_traveled\":6113.6,\"shape_pt_lat\":40.105440,\"shape_pt_lon\":-88.229014,\"shape_pt_sequence\":1089,\"stop_id\":\"ARYWRT:3\"},{\"shape_dist_traveled\":6134.8,\"shape_pt_lat\":40.105449,\"shape_pt_lon\":-88.228886,\"shape_pt_sequence\":1095},{\"shape_dist_traveled\":6440.5,\"shape_pt_lat\":40.108178,\"shape_pt_lon\":-88.228856,\"shape_pt_sequence\":1145,\"stop_id\":\"PLAZA:4\"},{\"shape_dist_traveled\":6683.1,\"shape_pt_lat\":40.110317,\"shape_pt_lon\":-88.228874,\"shape_pt_sequence\":1185},{\"shape_dist_traveled\":6773.4,\"shape_pt_lat\":40.110335,\"shape_pt_lon\":-88.227813,\"shape_pt_sequence\":1207,\"stop_id\":\"IU:1\"},{\"shape_dist_traveled\":6963.1,\"shape_pt_lat\":40.110477,\"shape_pt_lon\":-88.225810,\"shape_pt_sequence\":1272,\"stop_id\":\"GRNMAT:3\"},{\"shape_dist_traveled\":7142.6,\"shape_pt_lat\":40.110507,\"shape_pt_lon\":-88.223914,\"shape_pt_sequence\":1328},{\"shape_dist_traveled\":7271.4,\"shape_pt_lat\":40.111665,\"shape_pt_lon\":-88.223949,\"shape_pt_sequence\":1381,\"stop_id\":\"GDWNMRL:2\"},{\"shape_dist_traveled\":7590.8,\"shape_pt_lat\":40.114334,\"shape_pt_lon\":-88.224009,\"shape_pt_sequence\":1472,\"stop_id\":\"GWNMN:2\"},{\"shape_dist_traveled\":7702.2,\"shape_pt_lat\":40.115296,\"shape_pt_lon\":-88.224021,\"shape_pt_sequence\":1503,\"stop_id\":\"GWNCLK:2\"},{\"shape_dist_traveled\":7809.8,\"shape_pt_lat\":40.116228,\"shape_pt_lon\":-88.224038,\"shape_pt_sequence\":1551,\"stop_id\":\"UNIGWN:2\"},{\"shape_dist_traveled\":7837.4,\"shape_pt_lat\":40.116396,\"shape_pt_lon\":-88.224042,\"shape_pt_sequence\":1557},{\"shape_dist_traveled\":8025.7,\"shape_pt_lat\":40.116411,\"shape_pt_lon\":-88.221826,\"shape_pt_sequence\":1590,\"stop_id\":\"UNIAVE:1\"},{\"shape_dist_traveled\":8249.1,\"shape_pt_lat\":40.116427,\"shape_pt_lon\":-88.219374,\"shape_pt_sequence\":1638},{\"shape_dist_traveled\":8343.0,\"shape_pt_lat\":40.117273,\"shape_pt_lon\":-88.219382,\"shape_pt_sequence\":1673,\"stop_id\":\"LNCLNPK:2\"},{\"shape_dist_traveled\":8452.1,\"shape_pt_lat\":40.118151,\"shape_pt_lon\":-88.219392,\"shape_pt_sequence\":1694,\"stop_id\":\"LNCLNCHCH:2\"},{\"shape_dist_traveled\":8564.2,\"shape_pt_lat\":40.119050,\"shape_pt_lon\":-88.219419,\"shape_pt_sequence\":1702,\"stop_id\":\"LNCLNHIL:2\"},{\"shape_dist_traveled\":8677.0,\"shape_pt_lat\":40.119931,\"shape_pt_lon\":-88.219431,\"shape_pt_sequence\":1706,\"stop_id\":\"FRVWLNCLN:2\"},{\"shape_dist_traveled\":8966.3,\"shape_pt_lat\":40.122407,\"shape_pt_lon\":-88.219449,\"shape_pt_sequence\":1751,\"stop_id\":\"LNCLNWSCR:2\"},{\"shape_dist_traveled\":9104.4,\"shape_pt_lat\":40.123552,\"shape_pt_lon\":-88.219465,\"shape_pt_sequence\":1793,\"stop_id\":\"LNCLNSNST:2\"},{\"shape_dist_traveled\":9241.8,\"shape_pt_lat\":40.124684,\"shape_pt_lon\":-88.219480,\"shape_pt_sequence\":1821,\"stop_id\":\"ATRIUM:2\"},{\"shape_dist_traveled\":9533.4,\"shape_pt_lat\":40.127208,\"shape_pt_lon\":-88.219522,\"shape_pt_sequence\":1876,\"stop_id\":\"LNCLNBRAD:2\"},{\"shape_dist_traveled\":9829.4,\"shape_pt_lat\":40.129737,\"shape_pt_lon\":-88.219533,\"shape_pt_sequence\":1936,\"stop_id\":\"LNCLNKING:2\"},{\"shape_dist_traveled\":9860.3,\"shape_pt_lat\":40.129909,\"shape_pt_lon\":-88.219532,\"shape_pt_sequence\":1941},{\"shape_dist_traveled\":10003.5,\"shape_pt_lat\":40.129915,\"shape_pt_lon\":-88.217847,\"shape_pt_sequence\":1981,\"stop_id\":\"KPDLNVW:3\"},{\"shape_dist_traveled\":10015.6,\"shape_pt_lat\":40.129916,\"shape_pt_lon\":-88.217731,\"shape_pt_sequence\":1984},{\"shape_dist_traveled\":10267.1,\"shape_pt_lat\":40.132176,\"shape_pt_lon\":-88.217787,\"shape_pt_sequence\":2062},{\"shape_dist_traveled\":10287.2,\"shape_pt_lat\":40.132340,\"shape_pt_lon\":-88.217884,\"shape_pt_sequence\":2070,\"stop_id\":\"LNVWKLRNY:2\"},{\"shape_dist_traveled\":10303.6,\"shape_pt_lat\":40.132426,\"shape_pt_lon\":-88.217941,\"shape_pt_sequence\":2075},{\"shape_dist_traveled\":10366.3,\"shape_pt_lat\":40.132317,\"shape_pt_lon\":-88.218644,\"shape_pt_sequence\":2100,\"stop_id\":\"LNCLNKLRNY:1\"}]}");
            assertNotNull("points is null", points);
            assertEquals(40.099466, points.get(0).getLatitude());
            assertEquals(-88.220416, points.get(0).getLongitude());
        } catch (IOException e) {
            Log.e("TEST_ERROR", e.getMessage());
        }
    }
    @UiThreadTest
    public void testMap(){
        GoogleMap map = drawRouteActivity.getMap();
        drawRouteActivity.drawBusLocation(new Loc(0.1,0.1));
        CameraPosition position =map.getCameraPosition();
        LatLng pos = position.target;
        assertEquals(pos.latitude,0.10000007738025568);
        assertEquals(pos.longitude,0.10000012814998627);
    }
    public void testBuildVehicleJSONFalse() {
        try {
            Loc point = drawRouteActivity.buildVehicleJSON("{\"time\":\"2015-04-09T15:02:20-05:00\",\"new_changeset\":true,\"status\":{\"code\":404,\"msg\":\"Vehicle not found\"},\"rqst\":{\"params\":{}}}");
            assertNull(point);
        } catch (IOException e) {
            Log.e("TEST_ERROR", e.getMessage());
        }
    }
}