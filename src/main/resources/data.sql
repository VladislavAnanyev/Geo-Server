INSERT INTO PUBLIC.USERS (USERNAME, ACTIVATION_CODE, BALANCE, CHANGE_PASSWORD_CODE, DESCRIPTION, EMAIL, FIRST_NAME, LAST_NAME, ONLINE, PASSWORD, STATUS) VALUES ('user1', '0ae1eea8-64ed-4e6d-abb7-774a6780e834', 0, null, null, 'user1@ya.ru', 'user1', 'user1', 'false', '$2a$10$jPo1EgeMIRbhqKIBI.nDieIwEkAEfa6Qe.rze7gxUvdOeXAhnJrM6', false);
INSERT INTO PUBLIC.USERS (USERNAME, ACTIVATION_CODE, BALANCE, CHANGE_PASSWORD_CODE, DESCRIPTION, EMAIL, FIRST_NAME, LAST_NAME, ONLINE, PASSWORD, STATUS) VALUES ('user2', '9bbb9e90-da3f-4d12-a9c4-e38671975350', 0, null, null, 'user2@ya.ru', 'user2', 'user2', 'false', '$2a$10$lom.V0T23SaBmw/NwvpmbeS7oe7on/WwNIRDGEU2G4tKvMUok1CCW', false);
INSERT INTO PUBLIC.USERS (USERNAME, ACTIVATION_CODE, BALANCE, CHANGE_PASSWORD_CODE, DESCRIPTION, EMAIL, FIRST_NAME, LAST_NAME, ONLINE, PASSWORD, STATUS) VALUES ('user3', 'a25727a9-2535-4f2a-9217-937cb132a1b8', 0, null, null, 'user3@ya.ru', 'user3', 'user3', 'false', '$2a$10$3KABDvyY6BqPnbnk5uIakeihmUfNGapKTaIGUSelNMb12xgD7ro..', false);
INSERT INTO PUBLIC.USERS (USERNAME, ACTIVATION_CODE, BALANCE, CHANGE_PASSWORD_CODE, DESCRIPTION, EMAIL, FIRST_NAME, LAST_NAME, ONLINE, PASSWORD, STATUS) VALUES ('user4', '84c46339-ec53-4350-9dd3-749236e9ceb0', 0, null, null, 'user4@ya.ru', 'user4', 'user4', 'false', '$2a$10$XObwzYcEx.riseQHqskpou/2Njgg169HfeXsUagvYM4X8UbS9Lely', false);
INSERT INTO PUBLIC.USERS (USERNAME, ACTIVATION_CODE, BALANCE, CHANGE_PASSWORD_CODE, DESCRIPTION, EMAIL, FIRST_NAME, LAST_NAME, ONLINE, PASSWORD, STATUS) VALUES ('user5', 'bddd0c10-2eb0-4888-ba89-2651149993ae', 0, null, null, 'user5@ya.ru', 'user5', 'user5', 'false', '$2a$10$TIMIFI.Z0sQpjFnKUaFJOuY9d/lSgnTZHpKBGLWeMx5VXL5uJNqme', false);

INSERT INTO PUBLIC.USERS_ROLES (USERS_USERNAME, ROLES) VALUES ('user1', 'ROLE_USER');
INSERT INTO PUBLIC.USERS_ROLES (USERS_USERNAME, ROLES) VALUES ('user2', 'ROLE_USER');
INSERT INTO PUBLIC.USERS_ROLES (USERS_USERNAME, ROLES) VALUES ('user3', 'ROLE_USER');
INSERT INTO PUBLIC.USERS_ROLES (USERS_USERNAME, ROLES) VALUES ('user4', 'ROLE_USER');
INSERT INTO PUBLIC.USERS_ROLES (USERS_USERNAME, ROLES) VALUES ('user5', 'ROLE_USER');

INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (63, 'https://localhost/img/default.jpg', 'user1', 0);
INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (64, 'https://localhost/img/1.jpg', 'user1', 1);
INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (70, 'https://localhost/img/1.jpg', 'user1', 2);
INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (65, 'https://localhost/img/default.jpg', 'user2', 0);
INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (66, 'https://localhost/img/2.jpg', 'user2', 1);
INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (67, 'https://localhost/img/default.jpg', 'user3', 0);
INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (68, 'https://localhost/img/default.jpg', 'user4', 0);
INSERT INTO PUBLIC.USERS_PHOTOS (ID, URL, USER_USERNAME, POSITION) VALUES (69, 'https://localhost/img/default.jpg', 'user5', 0);

INSERT INTO PUBLIC.GEOLOCATIONS (ID, LAT, LNG, TIME, USERNAME) VALUES ( 1000, 55.863570, 37.537698, NOW(), 'user2');
INSERT INTO PUBLIC.GEOLOCATIONS (ID, LAT, LNG, TIME, USERNAME) VALUES ( 1001, 61.876225, 75.355473, NOW(), 'user3');

INSERT INTO PUBLIC.MEETINGS (ID, LAT, LNG, TIME, FIRST_USER_USERNAME, SECOND_USER_USERNAME) VALUES ( 990,  68.876225, 71.355473, NOW(), 'user3', 'user4');
INSERT INTO PUBLIC.MEETINGS (ID, LAT, LNG, TIME, FIRST_USER_USERNAME, SECOND_USER_USERNAME) VALUES ( 991,  62.876225, 81.355473, NOW(), 'user1', 'user2');
INSERT INTO PUBLIC.MEETINGS (ID, LAT, LNG, TIME, FIRST_USER_USERNAME, SECOND_USER_USERNAME) VALUES ( 992,  62.876225, 81.355473, NOW(), 'user1', 'user5');
INSERT INTO PUBLIC.MEETINGS (ID, LAT, LNG, TIME, FIRST_USER_USERNAME, SECOND_USER_USERNAME) VALUES ( 993,  62.876225, 81.355473, NOW(), 'user4', 'user2');



INSERT INTO PUBLIC.REQUESTS (ID, STATUS, MEETING_ID, MESSAGE_ID, SENDER_USERNAME, TO_USERNAME) VALUES (800, 'PENDING', 991, null, 'user1', 'user2');
INSERT INTO PUBLIC.REQUESTS (ID, STATUS, MEETING_ID, MESSAGE_ID, SENDER_USERNAME, TO_USERNAME) VALUES (801, 'ACCEPTED', 992, null, 'user1', 'user5');


INSERT INTO PUBLIC.USERS_FRIENDS (USERS_USERNAME, FRIENDS_USERNAME) VALUES ( 'user1', 'user5' );

INSERT INTO PUBLIC.DIALOGS (DIALOG_ID, IMAGE, NAME) VALUES (1196, null, null);
INSERT INTO PUBLIC.DIALOGS (DIALOG_ID, IMAGE, NAME) VALUES (1202, 'https://localhost/img/default.jpg', 'Группа');
INSERT INTO PUBLIC.DIALOGS (DIALOG_ID, IMAGE, NAME) VALUES (1223, null, null);
INSERT INTO PUBLIC.DIALOGS (DIALOG_ID, IMAGE, NAME) VALUES (1224, null, null);
INSERT INTO PUBLIC.DIALOGS (DIALOG_ID, IMAGE, NAME) VALUES (1277, null, null);


INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user3', 1202);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user3', 1223);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user2', 1196);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user2', 1202);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user2', 1224);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user1', 1196);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user1', 1202);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user1', 1223);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user1', 1277);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user4', 1224);
INSERT INTO PUBLIC.USERS_DIALOGS (USER_ID, DIALOG_ID) VALUES ('user4', 1277);


INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1197, '6', 'DELIVERED', '2021-12-06 01:16:52.815000', 1196, 'user1');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1198, '88', 'DELIVERED', '2021-12-06 01:16:59.978000', 1196, 'user2');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1199, '1', 'DELIVERED', '2021-12-06 01:17:29.995000', 1196, 'user1');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1200, '2', 'DELIVERED', '2021-12-06 01:17:30.484000', 1196, 'user1');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1201, '3', 'DELIVERED', '2021-12-06 01:17:30.945000', 1196, 'user1');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1203, 'Группа создана', 'DELIVERED', '2021-12-06 01:17:42.695000', 1202, 'user1');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1204, '4', 'DELIVERED', '2021-12-06 01:17:55.732000', 1202, 'user1');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1225, '44', 'DELIVERED', '2021-12-06 01:22:53.314000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1226, '55', 'DELIVERED', '2021-12-06 01:22:54.276000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1227, '3', 'DELIVERED', '2021-12-06 01:23:15.681000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1228, '4', 'DELIVERED', '2021-12-06 01:23:16.162000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1229, '5', 'DELIVERED', '2021-12-06 01:23:16.671000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1230, '6', 'DELIVERED', '2021-12-06 01:23:17.328000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1231, '7', 'DELIVERED', '2021-12-06 01:23:17.938000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1232, '8', 'DELIVERED', '2021-12-06 01:23:18.489000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1233, '9', 'DELIVERED', '2021-12-06 01:23:19.484000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1234, '10', 'DELIVERED', '2021-12-06 01:23:20.630000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1235, '11', 'DELIVERED', '2021-12-06 01:23:21.648000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1236, '12', 'DELIVERED', '2021-12-06 01:23:22.611000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1237, '13', 'DELIVERED', '2021-12-06 01:23:23.438000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1238, '14', 'DELIVERED', '2021-12-06 01:23:24.143000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1239, '15', 'DELIVERED', '2021-12-06 01:23:25.409000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1240, '16', 'DELIVERED', '2021-12-06 01:23:26.489000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1241, '17', 'DELIVERED', '2021-12-06 01:23:27.533000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1242, '18', 'DELIVERED', '2021-12-06 01:23:28.521000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1243, '19', 'DELIVERED', '2021-12-06 01:23:29.627000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1244, '20', 'DELIVERED', '2021-12-06 01:23:30.641000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1245, '21', 'DELIVERED', '2021-12-06 01:23:31.518000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1246, '22', 'DELIVERED', '2021-12-06 01:23:32.848000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1247, '23', 'DELIVERED', '2021-12-06 01:23:33.558000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1248, '24', 'DELIVERED', '2021-12-06 01:23:34.793000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1249, '25', 'DELIVERED', '2021-12-06 01:23:35.779000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1250, '26', 'DELIVERED', '2021-12-06 01:23:36.839000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1251, '27', 'DELIVERED', '2021-12-06 01:23:38.286000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1252, '28', 'DELIVERED', '2021-12-06 01:23:39.265000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1253, '29', 'DELIVERED', '2021-12-06 01:23:40.147000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1254, '30', 'DELIVERED', '2021-12-06 01:23:41.480000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1255, '21', 'DELIVERED', '2021-12-06 01:23:42.363000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1256, '32', 'DELIVERED', '2021-12-06 01:23:45.512000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1257, '33', 'DELIVERED', '2021-12-06 01:23:46.804000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1258, '34', 'DELIVERED', '2021-12-06 01:23:47.551000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1259, '35', 'DELIVERED', '2021-12-06 01:23:49.315000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1260, '36', 'DELIVERED', '2021-12-06 01:23:51.441000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1261, '37', 'DELIVERED', '2021-12-06 01:23:52.458000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1262, '38', 'DELIVERED', '2021-12-06 01:23:53.601000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1263, '39', 'DELIVERED', '2021-12-06 01:23:54.841000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1264, '40', 'DELIVERED', '2021-12-06 01:23:55.558000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1265, '41', 'DELIVERED', '2021-12-06 01:23:57.164000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1266, '42', 'DELIVERED', '2021-12-06 01:23:59.283000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1267, '43', 'DELIVERED', '2021-12-06 01:24:00.361000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1268, '44', 'DELIVERED', '2021-12-06 01:24:01.236000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1269, '45', 'DELIVERED', '2021-12-06 01:24:02.088000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1270, '46', 'DELIVERED', '2021-12-06 01:24:03.537000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1271, '47', 'DELIVERED', '2021-12-06 01:24:04.696000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1272, '48', 'DELIVERED', '2021-12-06 01:24:05.858000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1273, '49', 'DELIVERED', '2021-12-06 01:24:08.330000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1274, '50', 'DELIVERED', '2021-12-06 01:24:09.424000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1275, '51', 'DELIVERED', '2021-12-06 01:24:10.636000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1276, '52', 'DELIVERED', '2021-12-06 01:24:11.922000', 1224, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1278, '1', 'DELIVERED', '2021-12-06 01:25:09.141000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1279, 'asdf', 'DELIVERED', '2021-12-06 01:26:03.191000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1280, 'asdf', 'DELIVERED', '2021-12-06 01:26:03.396000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1281, 'sadf', 'DELIVERED', '2021-12-06 01:26:03.590000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1282, 'sad', 'DELIVERED', '2021-12-06 01:26:03.789000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1283, 'sd', 'DELIVERED', '2021-12-06 01:26:03.968000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1284, 'fdsaf', 'DELIVERED', '2021-12-06 01:26:04.163000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1285, 'sd', 'DELIVERED', '2021-12-06 01:26:04.351000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1286, 'fsad', 'DELIVERED', '2021-12-06 01:26:04.546000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1287, 'fsad', 'DELIVERED', '2021-12-06 01:26:04.732000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1288, 'sad', 'DELIVERED', '2021-12-06 01:26:04.939000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1289, 'fsda', 'DELIVERED', '2021-12-06 01:26:05.120000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1290, 'fsd', 'DELIVERED', '2021-12-06 01:26:05.318000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1291, 'fda', 'DELIVERED', '2021-12-06 01:26:05.521000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1292, 'fsa', 'DELIVERED', '2021-12-06 01:26:05.720000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1293, 'fdsa', 'DELIVERED', '2021-12-06 01:26:05.923000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1294, 'fdas', 'DELIVERED', '2021-12-06 01:26:06.129000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1295, 'fdsa', 'DELIVERED', '2021-12-06 01:26:06.334000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1296, 'fdas', 'DELIVERED', '2021-12-06 01:26:06.519000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1297, 'fs', 'DELIVERED', '2021-12-06 01:26:06.718000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1298, 'afsd', 'DELIVERED', '2021-12-06 01:26:06.919000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1299, 'fsd', 'DELIVERED', '2021-12-06 01:26:07.131000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1300, 'fdsa', 'DELIVERED', '2021-12-06 01:26:07.328000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1301, 'fs', 'DELIVERED', '2021-12-06 01:26:07.535000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1302, 'df', 'DELIVERED', '2021-12-06 01:26:07.744000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1303, 'sdafd', 'DELIVERED', '2021-12-06 01:26:07.934000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1304, 'sfs', 'DELIVERED', '2021-12-06 01:26:08.134000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1305, 'afd', 'DELIVERED', '2021-12-06 01:26:08.334000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1306, 'fs', 'DELIVERED', '2021-12-06 01:26:08.542000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1307, 'fa', 'DELIVERED', '2021-12-06 01:26:08.735000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1308, 'sfsd', 'DELIVERED', '2021-12-06 01:26:08.930000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1309, 'afs', 'DELIVERED', '2021-12-06 01:26:09.131000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1310, 'dafdsa', 'DELIVERED', '2021-12-06 01:26:09.326000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1311, 'fdsa', 'DELIVERED', '2021-12-06 01:26:09.532000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1312, 'f', 'DELIVERED', '2021-12-06 01:26:09.730000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1313, 'sdf', 'DELIVERED', '2021-12-06 01:26:09.912000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1314, 'sdfa', 'DELIVERED', '2021-12-06 01:26:10.126000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1315, 'sdf', 'DELIVERED', '2021-12-06 01:26:10.309000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1316, 'asf', 'DELIVERED', '2021-12-06 01:26:10.511000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1317, 'sf', 'DELIVERED', '2021-12-06 01:26:10.714000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1318, 'saf', 'DELIVERED', '2021-12-06 01:26:10.920000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1319, 's', 'DELIVERED', '2021-12-06 01:26:11.117000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1320, 'afs', 'DELIVERED', '2021-12-06 01:26:11.304000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1321, 'dfs', 'DELIVERED', '2021-12-06 01:26:11.506000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1322, 'df', 'DELIVERED', '2021-12-06 01:26:11.704000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1323, 'sdf', 'DELIVERED', '2021-12-06 01:26:11.905000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1324, 'asdf', 'DELIVERED', '2021-12-06 01:26:12.117000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1325, 'asdf', 'DELIVERED', '2021-12-06 01:26:12.327000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1326, 'dsf', 'DELIVERED', '2021-12-06 01:26:12.523000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1327, 'as', 'DELIVERED', '2021-12-06 01:26:12.724000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1328, 'a', 'DELIVERED', '2021-12-06 01:26:12.915000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1329, 'a', 'DELIVERED', '2021-12-06 01:26:13.106000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1330, 'as', 'DELIVERED', '2021-12-06 01:26:13.289000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1331, 'dfds', 'DELIVERED', '2021-12-06 01:26:13.502000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1332, 'fsd', 'DELIVERED', '2021-12-06 01:26:13.697000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1333, 'fd', 'DELIVERED', '2021-12-06 01:26:13.904000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1334, 'sfd', 'DELIVERED', '2021-12-06 01:26:14.122000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1335, 'sfas', 'DELIVERED', '2021-12-06 01:26:14.335000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1336, 'dfad', 'DELIVERED', '2021-12-06 01:26:14.520000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1337, 'sfd', 'DELIVERED', '2021-12-06 01:26:14.720000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1338, 'sfsda', 'DELIVERED', '2021-12-06 01:26:14.939000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1339, 'fas', 'DELIVERED', '2021-12-06 01:26:15.149000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1340, 'fasd', 'DELIVERED', '2021-12-06 01:26:15.392000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1341, 'fasd', 'DELIVERED', '2021-12-06 01:26:15.610000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1342, 'fasd', 'DELIVERED', '2021-12-06 01:26:15.832000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1343, 'fasd', 'DELIVERED', '2021-12-06 01:26:16.057000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1344, 'fds', 'DELIVERED', '2021-12-06 01:26:16.271000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1345, 'fasd', 'DELIVERED', '2021-12-06 01:26:16.482000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1346, 'fasd', 'DELIVERED', '2021-12-06 01:26:16.688000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1347, 'fsda', 'DELIVERED', '2021-12-06 01:26:16.875000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1348, 'fasd', 'DELIVERED', '2021-12-06 01:26:17.108000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1349, 'fsad', 'DELIVERED', '2021-12-06 01:26:17.304000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1350, 'fasd', 'DELIVERED', '2021-12-06 01:26:17.528000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1351, 'fsda', 'DELIVERED', '2021-12-06 01:26:17.750000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1352, 'fas', 'DELIVERED', '2021-12-06 01:26:17.951000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1353, 'fads', 'DELIVERED', '2021-12-06 01:26:18.170000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1354, 'fdas', 'DELIVERED', '2021-12-06 01:26:18.381000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1355, 'fsd', 'DELIVERED', '2021-12-06 01:26:18.596000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1356, 'fsda', 'DELIVERED', '2021-12-06 01:26:18.793000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1357, 'fasd', 'DELIVERED', '2021-12-06 01:26:19.011000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1358, 'fasd', 'DELIVERED', '2021-12-06 01:26:19.223000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1359, 'fdsa', 'DELIVERED', '2021-12-06 01:26:19.432000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1360, 'fasd', 'DELIVERED', '2021-12-06 01:26:19.636000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1361, 'fsda', 'DELIVERED', '2021-12-06 01:26:19.839000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1362, 'fsd', 'DELIVERED', '2021-12-06 01:26:20.018000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1363, 'afads', 'DELIVERED', '2021-12-06 01:26:20.229000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1364, 'fdsa', 'DELIVERED', '2021-12-06 01:26:20.434000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1365, 'fdas', 'DELIVERED', '2021-12-06 01:26:20.624000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1366, 'fasd', 'DELIVERED', '2021-12-06 01:26:20.831000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1367, 'fsd', 'DELIVERED', '2021-12-06 01:26:21.017000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1368, 'fds', 'DELIVERED', '2021-12-06 01:26:21.202000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1369, 'fds', 'DELIVERED', '2021-12-06 01:26:21.384000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1370, 'afas', 'DELIVERED', '2021-12-06 01:26:21.575000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1371, 'dfsd', 'DELIVERED', '2021-12-06 01:26:21.784000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1372, 'fas', 'DELIVERED', '2021-12-06 01:26:21.978000', 1277, 'user4');
INSERT INTO PUBLIC.MESSAGES (ID, CONTENT, STATUS, TIMESTAMP, DIALOG_ID, SENDER_USERNAME) VALUES (1373, 'f', 'DELIVERED', '2021-12-06 01:26:22.466000', 1277, 'user4');