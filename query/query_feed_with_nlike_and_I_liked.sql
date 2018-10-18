SELECT userid,t2.photoid,writing,accesslevel,picture, date, coalesce(nlike,0) as nlike, coalesce(liked,'') as liked FROM
(((select photo.userid,photo.photoid,writing,accesslevel,picture, photo.date
  from profile JOIN photo on photo.userid = profile.userid WHERE photo.userid = 'alexjane')
UNION
(select userid,photoid,writing,accesslevel,picture, date
 from (select fuserid FROM follow WHERE userid ='alexjane') as
      t1 JOIN photo on photo.userid = t1.fuserid))) as t2 LEFT JOIN
(SELECT userid as liked,nlike,t1.photoid FROM (SELECT userid,photoid FROM likes) as t1 join (SELECT count(*) as nlike,photoid FROM likes GROUP BY photoid) as t2 on t1.photoid = t2.photoid WHERE userid = 'alexjane') as t3 ON t2.photoid = t3.photoid ORDER BY date DESC

