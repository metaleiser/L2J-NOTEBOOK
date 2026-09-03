-- Party+AutoPlay spike (BotSpikeParty) DB provisioning.
-- DISPOSABLE: clones the ADMIN row (charId 268483035) for 8 bot ids.
-- account_name 'spikebot' is NOT required to exist in the accounts table;
-- Player.load(...) does not validate account_name. Rollback: DELETE by charId.
-- Recorded as RUNTIME EXECUTION EVIDENCE for the Party+AutoPlay spike.
-- Pre-test DB baseline (captured pre-provisioning):
--   characters: 1 row (268483035 ADMIN); items: 9 rows (owner=ADMIN); offline_play: 0 rows.

SET @src=268483035;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483100, 'SPIKEBOT01', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483101, 'SPIKEBOT02', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483102, 'SPIKEBOT03', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483103, 'SPIKEBOT04', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483104, 'SPIKEBOT05', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483105, 'SPIKEBOT06', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483106, 'SPIKEBOT07', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;

INSERT INTO l2jmobiush5.characters
  (account_name, charId, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, heading, x, y, z, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, accesslevel, online, onlinetime, char_slot, newbie,
   lastAccess, clan_privs, wantspeace, isin7sdungeon, power_grade, nobless, subpledge,
   lvl_joined_academy, apprentice, sponsor, clan_join_expiry_time, clan_create_expiry_time,
   death_penalty_level, bookmarkslot, vitality_points, createDate, language, faction, pccafe_points)
SELECT 'spikebot', 268483107, 'SPIKEBOT08', level, maxHp, curHp, maxCp, curCp, maxMp, curMp,
   face, hairStyle, hairColor, sex, 0, -81411, 246563, -3680, exp, expBeforeDeath, sp, karma,
   fame, pvpkills, pkkills, clanid, race, classid, base_class, transform_id, deletetime,
   cancraft, title, title_color, 0, 0, onlinetime, char_slot, newbie, lastAccess, clan_privs,
   wantspeace, isin7sdungeon, power_grade, nobless, subpledge, lvl_joined_academy, apprentice,
   sponsor, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, bookmarkslot,
   vitality_points, createDate, language, faction, pccafe_points
FROM l2jmobiush5.characters WHERE charId=@src;
