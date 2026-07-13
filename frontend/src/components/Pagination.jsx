function Pagination({ pageData, onPageChange, onPageSizeChange }) {
  if (!pageData.totalElements) return null

  const startPage = Math.max(0, pageData.page - 2)
  const endPage = Math.min(pageData.totalPages - 1, startPage + 4)
  const pages = Array.from({ length: endPage - startPage + 1 }, (_, index) => startPage + index)

  return (
    <nav className="pagination" aria-label="Employee pages">
      <label className="page-size">
        Rows per page
        <select value={pageData.size} onChange={(event) => onPageSizeChange(Number(event.target.value))}>
          {[5, 10, 20, 50, 100].map((size) => <option key={size} value={size}>{size}</option>)}
        </select>
      </label>
      <div className="page-controls">
        <button type="button" className="button button-secondary" onClick={() => onPageChange(pageData.page - 1)} disabled={pageData.first}>Previous</button>
        {pages.map((page) => (
          <button
            type="button"
            className={`page-button ${page === pageData.page ? 'active' : ''}`}
            key={page}
            onClick={() => onPageChange(page)}
            aria-current={page === pageData.page ? 'page' : undefined}
          >
            {page + 1}
          </button>
        ))}
        <button type="button" className="button button-secondary" onClick={() => onPageChange(pageData.page + 1)} disabled={pageData.last}>Next</button>
      </div>
    </nav>
  )
}

export default Pagination
