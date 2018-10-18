((select photo.userid,photo.photoid,writing,accesslevel,picture, photo.date from profile JOIN photo on photo.userid = profile.userid WHERE photo.userid = 'alexjane')
UNION
(select userid,photoid,writing,accesslevel,picture, date from (select fuserid FROM follow WHERE userid ='alexjane') as t1 JOIN photo on photo.userid = t1.fuserid)) ORDER BY date DESC
