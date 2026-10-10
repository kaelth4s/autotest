SELECT t FROM TestModel t
JOIN FETCH t.status
JOIN FETCH t.project
JOIN FETCH t.sessionModel
LEFT JOIN FETCH t.author
ORDER BY t.id DESC
LIMIT 1