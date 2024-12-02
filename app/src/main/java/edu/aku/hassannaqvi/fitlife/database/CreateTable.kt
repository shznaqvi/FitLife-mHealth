package edu.aku.hassannaqvi.fitlife.database


import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.ChildTable
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.ClusterTable
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.EntryLogTable
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.TestsTable
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.RandomHHTable
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.StreetsTable
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.TableDistricts
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.UsersTable


object CreateTable {


    const val SQL_CREATE_LISTING = ("CREATE TABLE "
            + TestsTable.TABLE_NAME + "("
            + TestsTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TestsTable.COLUMN_SESSION_ID + " TEXT,"
            + TestsTable.COLUMN_PROJECT_NAME + " TEXT,"
            + TestsTable.COLUMN_UID + " TEXT,"
            + TestsTable.COLUMN_STUID + " TEXT,"
            + TestsTable.COLUMN_DISTRICT_ID + " TEXT,"
            + TestsTable.COLUMN_AREA + " TEXT,"
            + TestsTable.COLUMN_CLUSTER_CODE + " TEXT,"
            + TestsTable.COLUMN_STREET_NUMBER + " TEXT,"
            + TestsTable.COLUMN_STRUCTURE_NUMBER + " TEXT,"
            + TestsTable.COLUMN_TAB_NUMBER + " TEXT,"
            + TestsTable.COLUMN_HHID + " TEXT,"
            + TestsTable.COLUMN_USERNAME + " TEXT,"
            + TestsTable.COLUMN_SYSDATE + " TEXT,"
            + TestsTable.COLUMN_ISTATUS + " TEXT,"
            + TestsTable.COLUMN_DEVICEID + " TEXT,"
            + TestsTable.COLUMN_GPSLAT + " TEXT,"
            + TestsTable.COLUMN_GPSLNG + " TEXT,"
            + TestsTable.COLUMN_GPSPRO + " TEXT,"
            + TestsTable.COLUMN_GPSDATE + " TEXT,"
            + TestsTable.COLUMN_GPSACC + " TEXT,"
            + TestsTable.COLUMN_SYNCED + " TEXT,"
            + TestsTable.COLUMN_SYNC_DATE + " TEXT,"
            + TestsTable.COLUMN_APPVERSION + " TEXT,"
            + TestsTable.COLUMN_STESTS + " TEXT"

            + ");")

    const val SQL_CREATE_STREETS = "CREATE TABLE " +
            StreetsTable.TABLE_NAME + " (" +
            StreetsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            StreetsTable.COLUMN_BL01 + " TEXT, " +
            StreetsTable.COLUMN_BL02 + " TEXT, " +
            StreetsTable.COLUMN_SST + " TEXT, " +
            StreetsTable.COLUMN_PROJECT_NAME + " TEXT, " +
            StreetsTable.COLUMN_UID + " TEXT, " +
            StreetsTable.COLUMN_DISTRICT_ID + " TEXT, " +
            StreetsTable.COLUMN_CLUSTER_CODE + " TEXT, " +
            StreetsTable.COLUMN_STREET_NUMBER + " TEXT, " +
            StreetsTable.COLUMN_USERNAME + " TEXT, " +
            StreetsTable.COLUMN_SYSDATE + " TEXT, " +
            StreetsTable.COLUMN_DEVICEID + " TEXT, " +
            StreetsTable.COLUMN_APPVERSION + " TEXT, " +
            StreetsTable.COLUMN_END_TIME + " TEXT, " +
            StreetsTable.COLUMN_SYNCED + " TEXT, " +
            StreetsTable.COLUMN_SYNC_DATE + " TEXT, " +
            StreetsTable.COLUMN_ENTRY_TYPE + " TEXT, " +
            StreetsTable.COLUMN_GPSLAT + " TEXT, " +
            StreetsTable.COLUMN_GPSLNG + " TEXT, " +
            StreetsTable.COLUMN_GPSDATE + " TEXT, " +
            StreetsTable.COLUMN_GPSACC + " TEXT, " +
            StreetsTable.COLUMN_ISTATUS + " TEXT, " +
            StreetsTable.COLUMN_ISTATUS96x + " TEXT" +
            ")"


    const val SQL_CREATE_ENTRYLOGS = ("CREATE TABLE "
            + EntryLogTable.TABLE_NAME + "("
            + EntryLogTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + EntryLogTable.COLUMN_PROJECT_NAME + " TEXT,"
            + EntryLogTable.COLUMN_UID + " TEXT,"
            + EntryLogTable.COLUMN_UUID + " TEXT,"
            + EntryLogTable.COLUMN_EB_CODE + " TEXT,"
            + EntryLogTable.COLUMN_HHID + " TEXT,"
            + EntryLogTable.COLUMN_USERNAME + " TEXT,"
            + EntryLogTable.COLUMN_SYSDATE + " TEXT,"
            + EntryLogTable.COLUMN_DEVICEID + " TEXT,"
            + EntryLogTable.COLUMN_ENTRY_DATE + " TEXT,"
            + EntryLogTable.COLUMN_IStatus + " TEXT,"
            + EntryLogTable.COLUMN_IStatus96x + " TEXT,"
            + EntryLogTable.COLUMN_ENTRY_TYPE + " TEXT,"
            + EntryLogTable.COLUMN_SYNCED + " TEXT,"
            + EntryLogTable.COLUMN_SYNC_DATE + " TEXT,"
            + EntryLogTable.COLUMN_APPVERSION + " TEXT"
            + " );"
            )



    const val SQL_CREATE_CHILD = ("CREATE TABLE "
            + ChildTable.TABLE_NAME + "("
            + ChildTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ChildTable.COLUMN_PROJECT_NAME + " TEXT, "
            + ChildTable.COLUMN_UID + " TEXT, "
            + ChildTable.COLUMN_UUID + " TEXT, "
/*
            + ChildTable.COLUMN_MUID + " TEXT, "
*/
            + ChildTable.COLUMN_EB_CODE + " TEXT, "
            + ChildTable.COLUMN_HHID + " TEXT, "
            + ChildTable.COLUMN_SNO + " TEXT, "
            + ChildTable.COLUMN_USERNAME + " TEXT, "
            + ChildTable.COLUMN_SYSDATE + " TEXT, "
            + ChildTable.COLUMN_CSTATUS + " TEXT, "
            + ChildTable.COLUMN_DEVICEID + " TEXT, "
            + ChildTable.COLUMN_DEVICETAGID + " TEXT, "
            + ChildTable.COLUMN_SYNCED + " TEXT, "
            + ChildTable.COLUMN_SYNC_DATE + " TEXT, "
            + ChildTable.COLUMN_APPVERSION + " TEXT, "
            + ChildTable.COLUMN_GPSLAT + " TEXT,"
            + ChildTable.COLUMN_GPSLNG + " TEXT,"
            + ChildTable.COLUMN_GPSDATE + " TEXT,"
            + ChildTable.COLUMN_GPSACC + " TEXT,"
            + ChildTable.COLUMN_SCH + " TEXT, "
            + ChildTable.COLUMN_SCB + " TEXT, "
            + ChildTable.COLUMN_SIM + " TEXT "
            + " );"
            )


    const val SQL_CREATE_USERS = ("CREATE TABLE "
            + UsersTable.TABLE_NAME + "("
            + UsersTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + UsersTable.COLUMN_LHWID + " TEXT,"
            + UsersTable.COLUMN_USERNAME + " TEXT,"
            + UsersTable.COLUMN_PASSWORD + " TEXT,"
            + UsersTable.COLUMN_FULLNAME + " TEXT,"
            + UsersTable.COLUMN_DISTRICT_ID + " TEXT,"
            + UsersTable.COLUMN_ENABLED + " TEXT,"
            + UsersTable.COLUMN_ISNEW_USER + " TEXT,"
            + UsersTable.COLUMN_PWD_EXPIRY + " TEXT,"
            + UsersTable.COLUMN_DESIGNATION + " TEXT"
            + " );"
            )


    const val SQL_CREATE_CLUSTERS = (
            "CREATE TABLE " + ClusterTable.TABLE_NAME + "("
                    + ClusterTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ClusterTable.COLUMN_DISTRICT_ID + " TEXT,"
                    + ClusterTable.COLUMN_DIST_NAME + " TEXT,"
                    + ClusterTable.COLUMN_AREA + " TEXT,"
                    + ClusterTable.COLUMN_CLUSTER_CODE + " TEXT"
                    + ");"
            )


    const val SQL_CREATE_DISTRICTS = ("CREATE TABLE "
            + TableDistricts.TABLE_NAME + "("
            + TableDistricts.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableDistricts.COLUMN_DISTRICT_NAME + " TEXT,"
            + TableDistricts.COLUMN_DISTRICT_ID + " TEXT"

            + " );"
            )

    const val SQL_CREATE_RANDOM_HH = ("CREATE TABLE " + RandomHHTable.TABLE_NAME + "("
            + RandomHHTable.COLUMN_ID + " TEXT,"
            + RandomHHTable.COLUMN_EB_CODE + " TEXT,"
            + RandomHHTable.COLUMN_LUID + " TEXT,"
            + RandomHHTable.COLUMN_HH_NO + " TEXT,"
            + RandomHHTable.COLUMN_STRUCTURE_NO + " TEXT,"
            + RandomHHTable.COLUMN_FAMILY_EXT_CODE + " TEXT,"
            + RandomHHTable.COLUMN_HH_HEAD + " TEXT,"
            + RandomHHTable.COLUMN_CONTACT + " TEXT,"
            + RandomHHTable.COLUMN_HH_SELECTED_STRUCT + " TEXT,"
            + RandomHHTable.COLUMN_RANDOMDT + " TEXT,"
            + RandomHHTable.COLUMN_SNO + " TEXT );"
            )

/*    const val SQL_CREATE_VERSIONAPP = ("CREATE TABLE "
            + VersionTable.TABLE_NAME + " ("
            + VersionTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + VersionTable.COLUMN_VERSION_CODE + " TEXT, "
            + VersionTable.COLUMN_VERSION_NAME + " TEXT, "
            + VersionTable.COLUMN_PATH_NAME + " TEXT "
            + ");"
            )*/

    const val SQL_ALTER_FORMS_GPS_LAT =
        ("ALTER TABLE " + TestsTable.TABLE_NAME + " ADD " + TestsTable.COLUMN_GPSLAT + " TEXT; ")
    const val SQL_ALTER_FORMS_GPS_LNG =
        ("ALTER TABLE " + TestsTable.TABLE_NAME + " ADD " + TestsTable.COLUMN_GPSLNG + " TEXT; ")
    const val SQL_ALTER_FORMS_GPS_DATE =
        ("ALTER TABLE " + TestsTable.TABLE_NAME + " ADD " + TestsTable.COLUMN_GPSDATE + " TEXT; ")
    const val SQL_ALTER_FORMS_GPS_ACC =
        ("ALTER TABLE " + TestsTable.TABLE_NAME + " ADD " + TestsTable.COLUMN_GPSACC + " TEXT; ")

    const val SQL_ALTER_CHILD_GPS_LAT =
        ("ALTER TABLE " + ChildTable.TABLE_NAME + " ADD " + ChildTable.COLUMN_GPSLAT + " TEXT; ")
    const val SQL_ALTER_CHILD_GPS_LNG =
        ("ALTER TABLE " + ChildTable.TABLE_NAME + " ADD " + ChildTable.COLUMN_GPSLNG + " TEXT; ")
    const val SQL_ALTER_CHILD_GPS_DATE =
        ("ALTER TABLE " + ChildTable.TABLE_NAME + " ADD " + ChildTable.COLUMN_GPSDATE + " TEXT; ")
    const val SQL_ALTER_CHILD_GPS_ACC =
        ("ALTER TABLE " + ChildTable.TABLE_NAME + " ADD " + ChildTable.COLUMN_GPSACC + " TEXT; ")
    const val SQL_ALTER_DISTRICT_CODE =
        ("ALTER TABLE " + TableDistricts.TABLE_NAME + " RENAME COLUMN district_code to "+ TableDistricts.COLUMN_DISTRICT_ID +" ; ")
    const val SQL_ALTER_DISTRICT_NAME =
        ("ALTER TABLE " + TableDistricts.TABLE_NAME + " RENAME COLUMN district_name to "+ TableDistricts.COLUMN_DISTRICT_NAME +" ; ")

}