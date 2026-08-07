-- 合同状态定时更新任务：每 5 秒检查一次未生效合同。
-- 使用 NOT EXISTS 保证脚本重复执行时不会生成重复任务。
INSERT INTO sys_job
    (job_name, job_group, invoke_target, cron_expression, misfire_policy,
     concurrent, status, create_by, create_time, remark)
SELECT
    '合同状态定时更新', 'DEFAULT', 'contractJob.updateContractStatusJob()',
    '0/5 * * * * ?', '3', '1', '0', 'admin', NOW(),
    '将开始日期已到达的未生效合同更新为已生效'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_job
    WHERE invoke_target = 'contractJob.updateContractStatusJob()'
);
