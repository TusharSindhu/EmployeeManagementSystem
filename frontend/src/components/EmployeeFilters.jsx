function EmployeeFilters({ filters, departments, managerOptions, onChange, onClear }) {
  return (
    <section className="filters" aria-label="Employee filters">
      <label>
        <span>Search</span>
        <input
          type="search"
          value={filters.search}
          onChange={(event) => onChange('search', event.target.value)}
          placeholder="Name or employee code"
        />
      </label>

      <label>
        <span>Department</span>
        <select value={filters.department} onChange={(event) => onChange('department', event.target.value)}>
          <option value="">All departments</option>
          {departments.map((department) => (
            <option key={department} value={department}>
              {department}
            </option>
          ))}
        </select>
      </label>

      <label>
        <span>Manager</span>
        <select value={filters.managerId} onChange={(event) => onChange('managerId', event.target.value)}>
          <option value="">All managers</option>
          {managerOptions.map((manager) => (
            <option key={manager.id} value={manager.id}>
              {manager.fullName} ({manager.employeeCode})
            </option>
          ))}
        </select>
      </label>

      <label>
        <span>Joining date from</span>
        <input
          type="date"
          value={filters.joiningDateFrom}
          onChange={(event) => onChange('joiningDateFrom', event.target.value)}
        />
      </label>

      <label>
        <span>Joining date to</span>
        <input
          type="date"
          value={filters.joiningDateTo}
          onChange={(event) => onChange('joiningDateTo', event.target.value)}
        />
      </label>

      <button type="button" className="button button-secondary filter-clear" onClick={onClear}>
        Clear filters
      </button>
    </section>
  )
}

export default EmployeeFilters
