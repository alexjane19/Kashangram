SELECT userid,t2.photoid,writing,accesslevel,picture, date, coalesce(nlike,0) as nlike FROM
(((select photo.userid,photo.photoid,writing,accesslevel,picture, photo.date
  from profile JOIN photo on photo.userid = profile.userid WHERE photo.userid = 'alexjane')
UNION
(select userid,photoid,writing,accesslevel,picture, date
 from (select fuserid FROM follow WHERE userid ='alexjane') as
      t1 JOIN photo on photo.userid = t1.fuserid))) as t2 LEFT JOIN
(SELECT count(*) as nlike,photoid FROM likes GROUP BY photoid) as t3 ON t2.photoid = t3.photoid ORDER BY date DESC
