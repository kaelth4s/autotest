FROM TestModel t
WHERE t.project.id = :project_id
ORDER BY t.${field} ${sort_type}