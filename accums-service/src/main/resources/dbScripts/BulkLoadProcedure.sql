CREATE OR REPLACE PROCEDURE INSERT_ (in ID int) 

BEGIN
    DECLARE LIMIT INTEGER;
	DECLARE COUNTER INTEGER;
	DECLARE AMOUNT INTEGER;
	DECLARE SUM_COUNTER INTEGER;
    DECLARE ACCUM_COUNTER INTEGER;
    DECLARE C INTEGER;
	DECLARE LINK INTEGER;
	SET ACCUM_COUNTER = 0;
    SET COUNTER = 1;
	SET SUM_COUNTER = 1;
	SET C = 1;
	SET LINK = NULL;
	SET LIMIT = SEQ_LDGR_HDR.nextval +  ID;
		insert into LDGR_HDR (LDGR_ID,DCN,CLM_LN_ID,SVC_ID,SVC_NM,SVC_DT, PROC_DT,NTWK_CD,NTWK_TIER_NM,PLN_ID,ALWD_AMT,MBR_ID,SUB_ID,UOM_NM,VEND_XACTN_ID)
	WITH LDGR_IDS(LDGR_ID) AS
	( VALUES(1) UNION ALL
	SELECT LDGR_ID+1 FROM LDGR_IDS WHERE LDGR_ID < LIMIT  - 1)
	select SEQ_LDGR_HDR.nextval, 'claim1002',1,10,TRANSLATE ( CHAR(BIGINT(RAND() * 1000)), 'abc', '123' ),CURRENT DATE - ((18 * 365) + RAND()*(47*365)) DAYS,
	CURRENT DATE - ((18 * 365) + RAND()*(47*365)) DAYS,'INB','PPO',10,rand()*100,CONCAT('A', cast(LDGR_ID as char(7))),TRANSLATE ( CHAR(BIGINT(RAND() * 1000)), 'abc', '123' ),'Dollars',
	'xxx' FROM LDGR_IDS;

	FOR V AS CUR1 CURSOR FOR 
        SELECT SVC_DT,UOM_NM,NTWK_CD,ALWD_AMT,LDGR_ID,MBR_ID,NTWK_TIER_NM,PLN_ID,SUB_ID FROM LDGR_HDR
    DO
	
	WHILE  C <= 3 DO
	
		INSERT INTO LDGR_ENTRY (LDGR_ENTRY_ID,PRIMY_LDGR_ENTRY_ID,LDGR_ID,ACCUM_TYP_NM,ROLE_NM,CST_SHR_TIER_NM,AMT,NTWK_CD,SNPSHT_SUM_AMT,UOM_NM,SVC_DT)
        VALUES (SEQ_LDGR_ENTRY.nextval,LINK,V.LDGR_ID,CONCAT('ACCUM',cast(ACCUM_COUNTER as char(7))),'PTNT','PPO',V.ALWD_AMT,V.NTWK_CD,V.ALWD_AMT,V.UOM_NM,V.SVC_DT);
        
			Select AMT into AMOUNT from ldgr_sum where NTWK_CD = v.NTWK_CD and NTWK_TIER_NM = V.NTWK_TIER_NM and ACCUM_TYP_NM = CONCAT('ACCUM',CAST(ACCUM_COUNTER as char(7))) and MBR_ID = v.MBR_ID;
			IF (AMOUNT  IS NULL) THEN
				INSERT INTO LDGR_SUM(LDGR_SUM_ID,LDGR_ID,ACCUM_TYP_NM,MBR_ID,NTWK_CD,NTWK_TIER_NM,END_DT,EFF_DT,PLN_ID,SUB_ID,AMT,MAX_AMT,MAX_VST_CNT,UOM_NM)
				values(SEQ_LDGR_SUM.nextval,V.LDGR_ID,CONCAT('ACCUM',cast(ACCUM_COUNTER as char(7))),V.MBR_ID,V.NTWK_CD,V.NTWK_TIER_NM,CURRENT TIMESTAMP, CURRENT DATE,V.PLN_ID,V.SUB_ID,V.ALWD_AMT,1000,100,V.UOM_NM);
			ELSE
				UPDATE LDGR_SUM SET AMT = AMOUNT + V.ALWD_AMT WHERE NTWK_CD = v.NTWK_CD and NTWK_TIER_NM = V.NTWK_TIER_NM and ACCUM_TYP_NM = CONCAT('ACCUM',cast(ACCUM_COUNTER as char(7))) and MBR_ID = v.MBR_ID;                                 
			END IF;
		IF  C = 1 THEN
        	SET LINK = COUNTER;
        ELSE IF C = 2 THEN
        	SET LINK = COUNTER - 1;		
        END IF;
        END IF;             
        SET COUNTER = COUNTER + 1;
        SET ACCUM_COUNTER = ACCUM_COUNTER + 1;
        SET C = C + 1;
	END WHILE;
	SET ACCUM_COUNTER = 0;
	SET LINK = NULL;
	SET C = 1;
    END FOR;	
END