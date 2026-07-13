const EMPLOYEES_URL = '/api/employees'

async function request(url, options = {}) {
  const response = await fetch(url, options)
  const hasJsonBody = response.headers.get('content-type')?.includes('application/json')
  const body = response.status === 204 ? null : hasJsonBody ? await response.json() : null

  if (!response.ok) {
    const error = new Error(body?.message || `Request failed with status ${response.status}`)
    error.status = response.status
    error.fieldErrors = body?.fieldErrors || {}
    throw error
  }

  return body
}

function addIfPresent(params, name, value) {
  if (value !== undefined && value !== null && value !== '') {
    params.set(name, value)
  }
}

export function getEmployees({
  search,
  department,
  managerId,
  joiningDateFrom,
  joiningDateTo,
  page = 0,
  size = 10,
  sortBy = 'joiningDate',
  direction = 'desc',
} = {}) {
  const params = new URLSearchParams()
  addIfPresent(params, 'search', search)
  addIfPresent(params, 'department', department)
  addIfPresent(params, 'managerId', managerId)
  addIfPresent(params, 'joiningDateFrom', joiningDateFrom)
  addIfPresent(params, 'joiningDateTo', joiningDateTo)
  addIfPresent(params, 'page', page)
  addIfPresent(params, 'size', size)
  addIfPresent(params, 'sortBy', sortBy)
  addIfPresent(params, 'direction', direction)

  return request(`${EMPLOYEES_URL}?${params.toString()}`)
}

export function getEmployeeById(id) {
  return request(`${EMPLOYEES_URL}/${id}`)
}

export function createEmployee(employee) {
  return request(EMPLOYEES_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(employee),
  })
}

export function updateEmployee(id, employee) {
  return request(`${EMPLOYEES_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(employee),
  })
}

export function deleteEmployee(id) {
  return request(`${EMPLOYEES_URL}/${id}`, { method: 'DELETE' })
}

export function getManagerOptions() {
  return request(`${EMPLOYEES_URL}/manager-options`)
}

export function getDepartments() {
  return request(`${EMPLOYEES_URL}/departments`)
}
